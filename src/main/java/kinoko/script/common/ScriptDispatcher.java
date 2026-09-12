package kinoko.script.common;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.map.PortalInfo;
import kinoko.server.ServerConfig;
import kinoko.server.node.ServerExecutor;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.FieldObject;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ScriptDispatcher {
    private static final Logger log = LogManager.getLogger(ScriptDispatcher.class);
    private static final ExecutorService scriptExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private static volatile Map<String, Method> scriptMap = new HashMap<>();
    private static volatile ScriptClassLoader hotClassLoader = null;

    public static void initialize() {
        final Map<String, Method> newScriptMap = new HashMap<>();
        // 从内置类路径扫描脚本
        scanBuiltinScripts(newScriptMap);
        // 从外部脚本目录加载热更新脚本（覆盖内置脚本）
        loadHotScripts(newScriptMap);
        scriptMap = newScriptMap;
        log.info("Initialized script dispatcher: {}", scriptMap.size());
    }

    /**
     * 热更新脚本 — 重新扫描内置脚本和外部脚本目录，替换 scriptMap。
     * 可在游戏运行中通过 GM 命令调用。
     *
     * @return 热更新结果信息
     */
    public static String reload() {
        try {
            final Map<String, Method> newScriptMap = new HashMap<>();
            // 重新扫描内置脚本
            scanBuiltinScripts(newScriptMap);
            final int builtinCount = newScriptMap.size();
            // 重新加载外部热更新脚本
            final int hotCount = loadHotScripts(newScriptMap);
            // 原子替换
            scriptMap = newScriptMap;
            log.info("Script hot-reload completed: {} builtin, {} hot-loaded, {} total",
                    builtinCount, hotCount, newScriptMap.size());
            return String.format("脚本热更新完成: 内置 %d 个, 热更新 %d 个, 总计 %d 个",
                    builtinCount, hotCount, newScriptMap.size());
        } catch (Exception e) {
            log.error("Script hot-reload failed", e);
            return "脚本热更新失败: " + e.getMessage();
        }
    }

    /**
     * 扫描内置类路径中的脚本。
     */
    private static void scanBuiltinScripts(Map<String, Method> targetMap) {
        final Reflections reflections = new Reflections("kinoko.script", Scanners.SubTypes);
        for (Class<? extends ScriptHandler> clazz : reflections.getSubTypesOf(ScriptHandler.class)) {
            registerScriptMethods(clazz, targetMap);
        }
    }

    /**
     * 从外部脚本目录加载热更新脚本类。
     * 脚本目录默认为工作目录下的 scripts/ 文件夹。
     * <p>
     * 使用方法：
     * 1. 修改 kinoko.script 包下的 .java 脚本文件
     * 2. 编译为 .class 文件，放到 scripts/kinoko/script/ 对应目录下
     * 3. 在游戏内执行 @reloadscripts 命令
     *
     * @return 热更新加载的脚本方法数量
     */
    private static int loadHotScripts(Map<String, Method> targetMap) {
        final Path scriptDir = Path.of(ServerConfig.SCRIPT_DIRECTORY);
        if (!Files.exists(scriptDir) || !Files.isDirectory(scriptDir)) {
            log.debug("外部脚本目录不存在，跳过热更新加载: {}", scriptDir.toAbsolutePath());
            return 0;
        }

        // 每次热更新创建新的 ClassLoader，旧的会被 GC 回收
        hotClassLoader = new ScriptClassLoader(scriptDir, ScriptDispatcher.class.getClassLoader());
        final List<String> classNames = hotClassLoader.scanScriptClasses();
        int hotCount = 0;

        for (String className : classNames) {
            // 跳过 common 包下的框架类
            if (className.startsWith("kinoko.script.common.")) {
                continue;
            }
            try {
                final Class<?> clazz = hotClassLoader.loadClass(className);
                if (ScriptHandler.class.isAssignableFrom(clazz)) {
                    @SuppressWarnings("unchecked")
                    final Class<? extends ScriptHandler> scriptClass = (Class<? extends ScriptHandler>) clazz;
                    final int before = targetMap.size();
                    registerScriptMethods(scriptClass, targetMap);
                    hotCount += targetMap.size() - before;
                    log.info("热更新加载脚本类: {}", className);
                }
            } catch (ClassNotFoundException e) {
                log.error("热更新加载脚本类失败: {}", className, e);
            }
        }

        return hotCount;
    }

    /**
     * 注册一个脚本类中所有带 @Script 注解的方法到目标 Map。
     * 如果 scriptName 已存在，热更新的脚本会覆盖内置脚本。
     */
    private static void registerScriptMethods(Class<? extends ScriptHandler> clazz, Map<String, Method> targetMap) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Script.class)) {
                continue;
            }
            if (method.getParameterCount() != 1 || method.getParameterTypes()[0] != ScriptManager.class) {
                log.error("脚本方法参数不正确: {}.{}", clazz.getName(), method.getName());
                continue;
            }
            final Script annotation = method.getAnnotation(Script.class);
            final String scriptName = annotation.value();
            if (targetMap.containsKey(scriptName)) {
                log.info("脚本 '{}' 被覆盖: {} -> {}.{}",
                        scriptName, targetMap.get(scriptName).getDeclaringClass().getName(),
                        clazz.getName(), method.getName());
            }
            targetMap.put(scriptName, method);
        }
    }

    public static void shutdown() {
        scriptExecutor.shutdown();
    }

    public static void startNpcScript(User user, FieldObject source, String scriptName, int speakerId) {
        startScript(ScriptType.NPC, scriptName, user, source, speakerId);
    }

    public static void startItemScript(User user, String scriptName, int speakerId) {
        startScript(ScriptType.ITEM, scriptName, user, user, speakerId);
    }

    public static void startQuestScript(User user, int questId, boolean isStart, int speakerId) {
        final String scriptName = String.format("q%d%s", questId, isStart ? "s" : "e");
        startScript(ScriptType.QUEST, scriptName, user, user, speakerId);
    }

    public static void startPortalScript(User user, PortalInfo portalInfo) {
        final String scriptName = portalInfo.getScript();
        startScript(ScriptType.PORTAL, scriptName, user, user, GameConstants.DEFAULT_SPEAKER_ID);
    }

    public static void startReactorScript(User user, Reactor reactor, String scriptName) {
        startScript(ScriptType.REACTOR, scriptName, user, reactor, GameConstants.DEFAULT_SPEAKER_ID);
    }

    public static void startFirstUserEnterScript(User user, String scriptName) {
        startScript(ScriptType.FIRST_USER_ENTER, scriptName, user, user, GameConstants.DEFAULT_SPEAKER_ID);
    }

    public static void startUserEnterScript(User user, String scriptName) {
        startScript(ScriptType.USER_ENTER, scriptName, user, user, GameConstants.DEFAULT_SPEAKER_ID);
    }

    private static void startScript(ScriptType scriptType, String scriptName, User user, FieldObject source, int speakerId) {
        // Check for existing dialog
        if (user.hasDialog()) {
            disposeScript(scriptType, user, source);
            return;
        }
        // Resolve script handler
        final Method handler = scriptMap.get(scriptName);
        if (handler == null) {
            user.write(MessagePacket.system("Not implemented, please let the GM know (" + scriptName + ")."));
            log.error("Could not resolve {} script with name : {}", scriptType, scriptName);
            disposeScript(scriptType, user, source);
            return;
        }
        // Execute script handler
        final Field field = source.getField();
        final ScriptManagerImpl scriptManager = new ScriptManagerImpl(user, field, source, scriptName, speakerId);
        scriptExecutor.submit(() -> {
            try {
                log.debug("Executing {} script : {}", scriptType.name(), scriptName);
                ServerExecutor.lockExecutor(field);
                handler.invoke(null, scriptManager);
            } catch (Exception e) {
                if (!(e.getCause() instanceof ScriptTermination)) {
                    log.error("Script execution failed with exception : {}", e.getCause(), e);
                    e.printStackTrace();
                }
            } finally {
                ServerExecutor.unlockExecutor(field);
                disposeScript(scriptType, user, source);
            }
        });
    }

    private static void disposeScript(ScriptType scriptType, User user, FieldObject source) {
        // Dispose item scripts and portal scripts if not warped
        if (scriptType == ScriptType.ITEM || (scriptType == ScriptType.PORTAL && user.getField() == source.getField())) {
            user.dispose();
        }
    }
}


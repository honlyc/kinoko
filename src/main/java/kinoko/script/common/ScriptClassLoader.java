package kinoko.script.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义 ClassLoader，用于从外部目录加载脚本类文件。
 * 每次热更新时创建新的实例，旧的 ClassLoader 会被 GC 回收。
 * <p>
 * 采用"子优先"（child-first）策略：优先从外部脚本目录加载类，
 * 如果找不到再委托给父 ClassLoader。这样可以覆盖已有的脚本类。
 */
public class ScriptClassLoader extends ClassLoader {
    private static final Logger log = LogManager.getLogger(ScriptClassLoader.class);
    private final Path scriptClassPath;

    public ScriptClassLoader(Path scriptClassPath, ClassLoader parent) {
        super(parent);
        this.scriptClassPath = scriptClassPath;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 先检查是否已经加载过
            Class<?> c = findLoadedClass(name);
            if (c != null) {
                return c;
            }

            // 只对 kinoko.script 包下的类（排除 common 子包）使用子优先策略
            if (name.startsWith("kinoko.script.") && !name.startsWith("kinoko.script.common.")) {
                try {
                    c = findClass(name);
                    if (resolve) {
                        resolveClass(c);
                    }
                    return c;
                } catch (ClassNotFoundException e) {
                    // 外部目录找不到，回退到父 ClassLoader
                }
            }

            // 委托给父 ClassLoader
            return super.loadClass(name, resolve);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String classFilePath = name.replace('.', '/') + ".class";
        Path classFile = scriptClassPath.resolve(classFilePath);

        if (Files.exists(classFile)) {
            try {
                byte[] classBytes = Files.readAllBytes(classFile);
                log.debug("从外部目录加载脚本类: {}", name);
                return defineClass(name, classBytes, 0, classBytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException("无法读取类文件: " + classFile, e);
            }
        }

        throw new ClassNotFoundException("在脚本目录中未找到类: " + name);
    }

    /**
     * 扫描脚本目录，返回所有 .class 文件对应的全限定类名。
     */
    public List<String> scanScriptClasses() {
        List<String> classNames = new ArrayList<>();
        if (!Files.exists(scriptClassPath)) {
            return classNames;
        }

        try {
            Files.walkFileTree(scriptClassPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".class")) {
                        // 将文件路径转换为全限定类名
                        Path relativePath = scriptClassPath.relativize(file);
                        String className = relativePath.toString()
                                .replace('/', '.')
                                .replace('\\', '.')
                                .replaceAll("\\.class$", "");
                        classNames.add(className);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("扫描脚本目录失败: {}", scriptClassPath, e);
        }

        return classNames;
    }
}

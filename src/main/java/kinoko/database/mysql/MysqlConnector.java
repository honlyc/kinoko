package kinoko.database.mysql;

import kinoko.database.*;
import kinoko.database.table.*;

import java.sql.Connection;
import java.sql.SQLException;

public final class MysqlConnector implements DatabaseConnector {
    private Connection connection;
    private IdAccessor idAccessor;
    private AccountAccessor accountAccessor;
    private CharacterAccessor characterAccessor;
    private FriendAccessor friendAccessor;
    private GuildAccessor guildAccessor;
    private GiftAccessor giftAccessor;
    private MemoAccessor memoAccessor;
    private MysqlActiveMachineAccessor activeMachineAccessor;
    private FameAccessor fameAccessor;

    @Override
    public IdAccessor getIdAccessor() {
        return idAccessor;
    }

    @Override
    public AccountAccessor getAccountAccessor() {
        return accountAccessor;
    }

    @Override
    public CharacterAccessor getCharacterAccessor() {
        return characterAccessor;
    }

    @Override
    public FriendAccessor getFriendAccessor() {
        return friendAccessor;
    }

    @Override
    public GuildAccessor getGuildAccessor() {
        return guildAccessor;
    }

    @Override
    public GiftAccessor getGiftAccessor() {
        return giftAccessor;
    }

    @Override
    public MemoAccessor getMemoAccessor() {
        return memoAccessor;
    }

    @Override
    public FameAccessor getFameAccessor() {
        return fameAccessor;
    }

    @Override
    public ActiveMachineAccessor getActiveMachineAccessor() {
        return activeMachineAccessor;
    }

    @Override
    public void initialize() {
        try {
            // Connect to SQLite database (creates file if it does not exist)
            connection = DatabaseConnection.getConnection();

            // Create Tables
            IdTable.createTable();
            AccountTable.createTable();
            CharacterTable.createTable();
            FriendTable.createTable();
            GuildTable.createTable();
            GiftTable.createTable();
            MemoTable.createTable();
            ActiveMachineTable.createTable();
            FameTable.createTable();

            // Create Accessors
            idAccessor = new MysqlIdAccessor();
            accountAccessor = new MysqlAccountAccessor();
            characterAccessor = new MysqlCharacterAccessor();
            friendAccessor = new MysqlFriendAccessor();
            guildAccessor = new MysqlGuildAccessor();
            giftAccessor = new MysqlGiftAccessor();
            memoAccessor = new MysqlMemoAccessor();
            activeMachineAccessor = new MysqlActiveMachineAccessor();
            fameAccessor = new MysqlFameAccessor();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize SQLite database", e);
        }
    }

    @Override
    public void shutdown() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to shutdown SQLite database", e);
        }
    }
}

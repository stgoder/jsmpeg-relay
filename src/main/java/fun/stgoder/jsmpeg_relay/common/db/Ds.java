package fun.stgoder.jsmpeg_relay.common.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fun.stgoder.jsmpeg_relay.common.Constants;
import fun.stgoder.jsmpeg_relay.common.exception.BaseException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Ds {
    public static BaseDataSource sqlite0;

    public static void initSqlite0() throws BaseException {
        try {
            File dbFile = new File(Constants.DS_SQLITE0_DB_FILE_PATH);
            if (!dbFile.exists()) {
                try (final InputStream slqite0InputStream = Ds.class.getResourceAsStream("/sqlite0.db")) {
                    if (slqite0InputStream == null)
                        throw new IOException("read sqlite0 db file in resources err");
                    FileUtils.copyInputStreamToFile(slqite0InputStream, dbFile);
                }
                System.out.println("db file not found, use empty db file");
            }
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:" + Constants.DS_SQLITE0_DB_FILE_PATH);
            config.setPoolName(Constants.DS_SQLITE0_POOL_NAME);
            config.setMaximumPoolSize(Constants.DS_SQLITE0_POOL_SIZE);
            HikariDataSource hikariDataSource = new HikariDataSource(config);
            sqlite0 = new BaseDataSource();
            sqlite0.init(hikariDataSource);
        } catch (Exception e) {
            System.out.println("init sqlite0: "
                    + Constants.DS_SQLITE0_DB_FILE_PATH + " failed, work without sqlite0 data source");
            throw new BaseException(-1, "init data source err");
        }
    }

    public static void main(String[] args) throws BaseException {
        Ds.initSqlite0();
        //Ds.sqlite0.dropTableIfExists("pusher");
        //Ds.sqlite0.createTableFromBean(PusherEntity.class);
    }
}

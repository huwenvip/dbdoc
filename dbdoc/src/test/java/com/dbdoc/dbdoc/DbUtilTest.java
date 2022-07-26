package com.dbdoc.dbdoc;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class DbUtilTest {
    
//    private static final Set<String> DB_NAME = Set.of("xndb_archive", "xndb_bpmn", "xndb_business", "xndb_cm", "xndb_contract", "xndb_customer",
//            "xndb_fin", "xndb_org", "xndb_orgauth", "xndb_risk", "xndb_standard", "xndb_system");
//        private static final Set<String> DB_NAME = Set.of("xndb_bpmn");
        private static final Set<String> DB_NAME = new HashSet<String>(Arrays.asList("xndb_bpmn"));


    @Test
    public void shouldAnswerWithTrue() {
        //数据源
        String outputDir = "C:\\Users\\huwen\\Desktop\\数据库文档";
        String htmlOutputDir = "C:\\Users\\huwen\\Desktop\\数据库文档html";
        for (String dbName : DB_NAME) {
            executeDocument(dbName, EngineFileType.WORD, outputDir);
            executeDocument(dbName, EngineFileType.HTML, htmlOutputDir);
        }
    }
    
    private void executeDocument(String dbname, EngineFileType engineFileType, String outputDir) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        hikariConfig.setJdbcUrl("jdbc:sqlserver://192.168.11.229:1433;Database=cqzj-boot" );
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("tc9ol.)P:?");
        //设置可以获取tables remarks信息
        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaximumPoolSize(5);
        DataSource dataSource = new HikariDataSource(hikariConfig);
        //生成配置
        EngineConfig engineConfig = EngineConfig.builder()
                //生成文件路径
                .fileOutputDir(outputDir)
                //打开目录
                .openOutputDir(true)
                // TODO 文件类型
                .fileType(engineFileType)
                //生成模板实现
                .produceType(EngineTemplateType.freemarker).build();
        
        //忽略表
        ArrayList<String> ignoreTableName = new ArrayList<>();
        ignoreTableName.add("act_");
        
        //忽略表前缀
        ArrayList<String> ignorePrefix = new ArrayList<>();
        ignorePrefix.add("act_");
        //忽略表后缀
        ArrayList<String> ignoreSuffix = new ArrayList<>();
        ignoreSuffix.add("_test");
        ProcessConfig processConfig = ProcessConfig.builder()
                //忽略表名
                .ignoreTableName(ignoreTableName)
                //忽略表前缀
                .ignoreTablePrefix(ignorePrefix)
                //忽略表后缀
                .ignoreTableSuffix(ignoreSuffix).build();
        //配置
        Configuration config = Configuration.builder()
                //版本
                .version("1.0.0")
                //描述
                .description("业务系统同数据文档")
                //数据源
                .dataSource(dataSource)
                //生成配置
                .engineConfig(engineConfig)
                //生成配置
                .produceConfig(processConfig).build();
        //执行生成
        new DocumentationExecute(config).execute();
    }
}

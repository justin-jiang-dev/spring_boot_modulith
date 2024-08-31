package com.nuoson.modulith;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * 生成 Mybatis plus 代码 运行指令 -- ./mvnw surefire:test -Dtest=MyBatisPlusGenerator
 * 生成器文档:
 * https://baomidou.com/pages/779a6e/#%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8
 * 生成器源代码：
 * https://github.com/baomidou/mybatis-plus/tree/3.0/mybatis-plus-generator
 */
@SuppressWarnings("PMD.TestClassShouldEndWithTestNamingRule")
@SpringBootTest
class MyBatisPlusGenerator {

    private static final String ROOT_PACKAGE = "com.nuoson.modulith";
    private static final String INFRA_PACKAGE = String.format("%s.infra", ROOT_PACKAGE);

    @Test
    void generateInventoryFromH2() throws Exception {
        // 生成代码基础包名
        String basePackageName = String.format("%s.internal.inventory", INFRA_PACKAGE);
        // 目标表格
        List<String> targetTableNames = Arrays.asList("inventory");
        // 表格前缀，生成类时，会去掉前缀
        List<String> targetTableNamePrefixes = Arrays.asList();

        DataSourceConfig dsConfig = prepareH2Schema();
        AutoGenerator generator = new AutoGenerator(dsConfig);

        System.out.println("generating database access codes ...");
        // 生成代码
        generate(generator, basePackageName, targetTableNames, targetTableNamePrefixes);

    }

    @Test
    void generateOrderFromH2() throws Exception {
        // 生成代码基础包名
        String basePackageName = String.format("%s.internal.order", INFRA_PACKAGE);
        // 目标表格
        List<String> targetTableNames = Arrays.asList("product_order");
        // 表格前缀，生成类时，会去掉前缀
        List<String> targetTableNamePrefixes = Arrays.asList();

        DataSourceConfig dsConfig = prepareH2Schema();
        AutoGenerator generator = new AutoGenerator(dsConfig);

        System.out.println("generating database access codes ...");
        // 生成代码
        generate(generator, basePackageName, targetTableNames, targetTableNamePrefixes);

    }

    // #region [** 私有方法 ** ] -- ** Input Description **
    /**
     * 
     * @return
     * @throws Exception
     */
    private DataSourceConfig prepareH2Schema() throws Exception {
        // 准备数据库
        System.out.println("preparing H2 Database Schema ...");
        String jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;";
        String jdbcUserName = "root";
        String jdbcPassword = "test";
        DataSourceConfig dsConfig = new DataSourceConfig.Builder(jdbcUrl, jdbcUserName, jdbcPassword)
                .build();
        try (Connection conn = dsConfig.getConn()) {
            // 创建表格
            InputStream schemaStream = MyBatisPlusGenerator.class.getResourceAsStream("/db/schema-h2.sql");
            assertNotNull(schemaStream);
            ScriptRunner schemaRunner = new ScriptRunner(conn);
            schemaRunner.setAutoCommit(true);
            schemaRunner.runScript(new InputStreamReader(schemaStream));
            // 创建数据
            InputStream dataStream = MyBatisPlusGenerator.class.getResourceAsStream("/db/data-h2.sql");
            ScriptRunner dataRunner = new ScriptRunner(conn);
            dataRunner.setAutoCommit(true);
            dataRunner.runScript(new InputStreamReader(dataStream));
        }
        return dsConfig;
    }

    /**
     * 通用生成方法
     * 
     * @param jdbcUrl
     * @param jdbcUserName
     * @param jdbcPassword
     * @param basePackageName
     * @param targetTableNames
     * @param targetTableNamePrefixes
     */
    private void generate(
            AutoGenerator generator,
            String basePackageName,
            List<String> targetTableNames,
            List<String> targetTableNamePrefixes) {
        // 输出根目录
        String outputDir = "./src/main/java";
        generator.global(new GlobalConfig.Builder()
                // 设置作者
                .author("nuoson auto")
                // 设置日期格式
                .commentDate("yyyy-MM-dd HH:mm:ss")
                // 设置日期类型
                .dateType(DateType.TIME_PACK)
                // 指定输出目录
                .outputDir(outputDir)
                .disableOpenDir().build());
        generator.packageInfo(new PackageConfig.Builder()
                // 设置父包名
                .parent(basePackageName)
                .moduleName("repository")
                // 自定义 DO 目录
                .entity("model")
                // 可以通过 pathInfo 定制指定对象（DO, Mapper等）输出路径， 例如：
                // .pathInfo(Collections.singletonMap(OutputFile.mapperXml, outputDir))
                .build());
        generator.strategy(new StrategyConfig.Builder()
                .addInclude(targetTableNames)
                // 设置表名前缀，生成类文件时，会去掉前缀
                .addTablePrefix(targetTableNamePrefixes)
                // 设置 DO 生成规则
                .entityBuilder()
                // DO 会被覆盖，不能手动编辑
                .enableFileOverride()
                .formatFileName("%sDO")
                .disableSerialVersionUID()
                .enableLombok()
                // 生成 @TableField 注解，用于解决个别数据库字段名不符下划线命名规范的问题
                .enableTableFieldAnnotation()
                .build()
                // 设置 Mapper 生成规则
                .mapperBuilder()
                // Mapper 会被覆盖，不能手动编辑
                .enableFileOverride()
                // 添加 @Mapper 注解
                .mapperAnnotation(org.apache.ibatis.annotations.Mapper.class)
                .build()
                // 设置 Service 生成规则
                .serviceBuilder()
                // Service 会被覆盖，不能手动编辑
                .enableFileOverride()
                .formatServiceFileName("%sDatabaseService")
                .build()
                .serviceBuilder()
                .enableFileOverride()
                .formatServiceImplFileName("%sDatabaseServiceImpl")
                .build()
                .controllerBuilder().disable()
                .entityBuilder().javaTemplate("./templates/entity_auto.java")
                .mapperBuilder().disableMapperXml().mapperTemplate("./templates/mapper_auto.java")
                .serviceBuilder()
                .serviceTemplate("./templates/service_auto.java")
                .serviceImplTemplate("./templates/serviceImpl_auto.java")
                .build());

        // 使用Freemarker引擎模板，默认的是Velocity引擎模板
        generator.execute(new FreemarkerTemplateEngine());
    }
    // #endregion

}

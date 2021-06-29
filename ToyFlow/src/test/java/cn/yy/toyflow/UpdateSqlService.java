package cn.yy.toyflow;

import org.apache.commons.codec.Charsets;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Connection;

/**
 * mybatis执行SQL脚本
 */
@Component
public class UpdateSqlService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataSource dataSource;

    /**
     * 使用ScriptRunner执行SQL脚本
     */
    public void doExecuteSql() {
        //通过数据源获取数据库链接
        Connection connection = DataSourceUtils.getConnection(dataSource);
        //创建脚本执行器
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        //创建字符输出流，用于记录SQL执行日志
        StringWriter writer = new StringWriter();
        PrintWriter print = new PrintWriter(writer);
        //设置执行器日志输出
        scriptRunner.setLogWriter(print);
        //设置执行器错误日志输出
        scriptRunner.setErrorLogWriter(print);
        //设置读取文件格式
        Resources.setCharset(Charsets.UTF_8);
        String[] filePaths = {"scripts/init.sql"};
        for (String path : filePaths) {

            Reader reader = null;
            try {
                //获取资源文件的字符输入流
                reader = Resources.getResourceAsReader(path);
            } catch (IOException e) {
                //文件流获取失败，关闭链接
                logger.error(e.getMessage(), e);
                scriptRunner.closeConnection();
                return;
            }
            //执行SQL脚本
            scriptRunner.runScript(reader);
            //关闭文件输入流
            try {
                reader.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        //输出SQL执行日志
        logger.debug(writer.toString());
        //关闭输入流
        scriptRunner.closeConnection();
    }
}
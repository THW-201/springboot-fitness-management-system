package com.fitness.interceptor;

import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.entity.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Properties;

/**
 * MyBatis数据隔离拦截器
 * 自动为教练角色的查询添加数据隔离条件
 * 确保教练只能查询自己负责的学生数据
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DataIsolationInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        
        // 获取BoundSql
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        String originalSql = boundSql.getSql();
        
        // 获取MappedStatement
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        String mappedStatementId = mappedStatement.getId();
        
        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        
        // 只对教练角色进行数据隔离
        if (loginUser != null && loginUser.getRoles() != null && loginUser.getRoles().contains("ROLE_COACH")) {
            // 获取教练ID
            Long coachId = getUserId(loginUser);
            
            if (coachId != null && shouldApplyDataIsolation(mappedStatementId, originalSql)) {
                try {
                    String modifiedSql = addCoachDataIsolation(originalSql, coachId);
                    metaObject.setValue("delegate.boundSql.sql", modifiedSql);
                    log.debug("Applied data isolation for coach {}: {}", coachId, modifiedSql);
                } catch (Exception e) {
                    log.error("Failed to apply data isolation: {}", e.getMessage());
                    // 如果SQL解析失败，继续执行原SQL
                }
            }
        }
        
        return invocation.proceed();
    }

    /**
     * 判断是否需要应用数据隔离
     */
    private boolean shouldApplyDataIsolation(String mappedStatementId, String sql) {
        // 只对查询学生相关数据的SQL应用数据隔离
        String lowerSql = sql.toLowerCase();
        
        // 检查是否是查询学生表的SQL
        if (lowerSql.contains("from student_profiles") || 
            lowerSql.contains("from reservations") ||
            lowerSql.contains("from check_ins") ||
            lowerSql.contains("from health_plans")) {
            
            // 排除已经包含coach_id条件的SQL（避免重复添加）
            if (lowerSql.contains("coach_id")) {
                return false;
            }
            
            // 排除特定的Mapper方法（如findByCoachId已经包含coach_id条件）
            if (mappedStatementId.contains("findByCoachId") ||
                mappedStatementId.contains("getCoachIdByStudentId")) {
                return false;
            }
            
            return true;
        }
        
        return false;
    }

    /**
     * 为SQL添加教练数据隔离条件
     */
    private String addCoachDataIsolation(String originalSql, Long coachId) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(originalSql);
        
        if (statement instanceof Select) {
            Select select = (Select) statement;
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            
            // 创建 coach_id = ? 条件
            EqualsTo equalsTo = new EqualsTo();
            equalsTo.setLeftExpression(new Column("coach_id"));
            equalsTo.setRightExpression(new LongValue(coachId));
            
            // 将新条件添加到WHERE子句
            Expression where = plainSelect.getWhere();
            if (where != null) {
                AndExpression andExpression = new AndExpression(where, equalsTo);
                plainSelect.setWhere(andExpression);
            } else {
                plainSelect.setWhere(equalsTo);
            }
            
            return select.toString();
        }
        
        return originalSql;
    }

    /**
     * 从登录用户中获取用户ID
     */
    private Long getUserId(LoginUser loginUser) {
        if (loginUser == null) {
            return null;
        }
        return loginUser.getUserId();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以从配置文件读取属性
    }
}

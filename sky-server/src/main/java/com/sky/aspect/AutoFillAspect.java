package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 自訂義切面，實現公共字段填充處理邏輯
 */
@Aspect //切面
@Component //Bean 給spring容器管理
@Slf4j  //紀錄日誌
public class AutoFillAspect {

    /**
     * 切入點 (對某些類的某些方法攔截)
     * 對mapper包內所有的方法 && 有AutoFill註解的方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在通知中進行公共字段的賦值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("開始進行公共字段自動填充...");

        //1.獲取當前被攔截的方法上的數據庫操作類型
        MethodSignature signature = (MethodSignature)joinPoint.getSignature(); //方法簽名對象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); //獲得方法上的註解對象
        OperationType operationType = autoFill.value();//獲得數據庫操作類型


        //2.獲取當前攔截方法的參數--實體對象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0 ){
            return;
        }

        Object entity = args[0]; //獲取第一個參數(實體對象)

        //3.準備賦值的數據
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //4.根據不同操作類型，為對應類型通過反射賦值
        if(operationType == OperationType.INSERT){
            //為4個公共字段填充
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDate.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通過反射為對象屬性賦值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {
            //為2個公共字段填充
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通過反射為對象屬性賦值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

package com.nuoson.modulith;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ValidationTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void setup() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void destroy() {
        validatorFactory.close();
    }

    // #region [** 场景1 ** ] -- ** 约束定义累计原则 **
    /**
     * 基类
     */
    private class User {
        @NotBlank(message = "base -- privateValue should not be blank")
        private String privateValue;

        /**
         * 属性
         * 
         * @return
         */
        @Size(min = 10, message = "base -- User name(${validatedValue}) length should be equal or greater than {min}")
        public String getName() {
            return "user";
        }

        @Min(value = 15, message = "base -- User calcAge(${validatedValue}) returned value should be equal or less than {value}")
        public Integer calcAge() {
            return 5;
        }

        @Min(value = 2, message = "base -- User checkBaseOnlyMethod(${validatedValue}) returned value should be equal or more than {value}")
        public Integer checkBaseOnlyMethod() {
            return 1;
        }
    }

    /**
     * 子类
     */
    private class Customer extends User {
        /**
         * 私有变量重名
         */
        @NotBlank(message = "child -- privateValue should not be blank")
        private String privateValue;

        /**
         * 属性覆盖
         */
        @Size(max = 7, message = "child -- Customer name(${validatedValue}) length should be equal or less than {max}")
        public String getName() {
            return "customer";
        }

        /**
         * 方法覆盖
         */
        @Min(value = 18, message = "child -- Customer age(${validatedValue}) length should be equal or less than {value}")
        @Override
        public Integer calcAge() {
            return 10;
        }
    }

    @Test
    public void verifyCumulativeConstraints() throws Exception {
        Set<ConstraintViolation<Customer>> violations = null;
        Customer customerObj = new Customer();

        System.out.println("** 私有字段约束累计 **");
        violations = validator.validateProperty(customerObj, "privateValue");
        printViolationMessage(violations);

        System.out.println("** 属性约束累计 **");
        violations = validator.validateProperty(customerObj, "name");
        printViolationMessage(violations);

        System.out.println("** 方法约束累计 **");
        violations = validator.forExecutables().validateReturnValue(
                customerObj, Customer.class.getMethod("calcAge"), customerObj.calcAge());
        printViolationMessage(violations);

        System.out.println("** 递归发现验证 **");
        violations = validator.forExecutables().validateReturnValue(
                customerObj, Customer.class.getMethod("checkBaseOnlyMethod"), customerObj.checkBaseOnlyMethod());
        printViolationMessage(violations);

    }
    // #endregion

    // #region [** 场景2 ** ] -- ** 约束执行顺序和级联 **

    @Data
    private class Cap {
        @NotBlank(message = "cap -- color should not be blank")
        String color;
    }

    @Data
    private class Food {
        @NotBlank(message = "food -- name should not be blank")
        String name;

        @NotNull(message = "food -- count should not be null")
        @Positive(message = "food -- count should not be positive, but current is ${validatedValue}")
        Integer count;
    }

    @Data
    private class Animal {
        /**
         * 约束级联
         */
        @Valid
        @NotNull(message = "base -- cap should not be null")
        private Cap cap;

        @NotBlank(message = "base -- name should not be blank")
        private String name;

        @NotBlank(message = "base -- type should not be blank")
        private String type;

        /**
         * 容器内对象的约束级联
         */
        @NotNull(message = "base -- food should not be null")
        private List<@Valid Food> food;

    }

    /**
     * 约束执行顺序和级联验证
     */
    @Test
    public void verifyValidationOrderAndCascade() {
        Set<ConstraintViolation<Animal>> violations = null;
        Animal animalObj = new Animal();
        animalObj.setCap(new Cap());
        animalObj.setFood(List.of(new Food()));

        System.out.println("** 约束执行顺序和级联验证 **");
        violations = validator.validate(animalObj);
        printViolationMessage(violations);
    }
    // #endregion

    // #region [** 场景3 ** ] -- ** 约束组合 **

    @Data
    class Address {
        @CustomZipCode
        private String zipCode;
    }

    @Test
    public void verifyComposition() {
        Set<ConstraintViolation<Address>> violations = null;
        Address addrObj = new Address();
        addrObj.setZipCode("1234");
        System.out.println("** 约束组合验证 **");
        violations = validator.validate(addrObj);
        printViolationMessage(violations);
    }

    // #endregion

    // #region [** 场景4 ** ] -- ** 约束元数据获取 **

    // #endregion

    // #region [** 场景5 ** ] -- ** 自定义验证器 **

    // #endregion

    // #region [** 私有方法 ** ] -- ** Input Description **
    /**
     * 
     * @param violations
     */
    private <T> void printViolationMessage(Set<ConstraintViolation<T>> violations) {
        violations.forEach(item -> {
            System.out.println(item.getMessage());
        });
    }
    // #endregion
}

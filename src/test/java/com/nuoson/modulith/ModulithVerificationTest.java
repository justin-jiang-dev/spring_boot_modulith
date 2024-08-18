package com.nuoson.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

@SuppressWarnings({ "PMD.TestClassShouldEndWithTestNamingRule", "java:S5786" })
class ModulithVerificationTest {
    /**
     * 内部文件命名
     */
    private static final String INTERNAL = "internal";

    /**
     * 模块依赖关系验证，通过如下指令执行此测试用例：
     * 
     * ./mvnw surefire:test -Dtest=ModulithVerificationTests
     */
    @Test
    public void verify() {
        ApplicationModules modules = ApplicationModules.of(Application.class);
        // 验证内部文件夹不能暴露给外部模块
        modules.forEach(moduleItem -> {
            String internalFolder = String.format("%s.%s", moduleItem.getBasePackage().getName(), INTERNAL);
            moduleItem.getNamedInterfaces().forEach(namedInterface -> {
                if (namedInterface.isNamed()) {
                    namedInterface.forEach(classItem -> {
                        if (internalFolder.equals(classItem.getPackageName())) {
                            throw new RuntimeException(
                                    String.format("%s 文件夹(%s)不能暴露给其他模块", INTERNAL, classItem.getPackageName()));
                        }
                        System.out.println(classItem.getPackageName());
                    });
                }
            });
        });
        // 验证模块依赖关系
        modules.verify();
    }

}

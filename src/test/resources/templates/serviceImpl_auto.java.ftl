package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
<#if table.serviceInterface>
import ${package.Service}.${table.serviceName};
</#if>
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;

/**
 * <p>
 * ${table.comment!} 服务实现类
  * ### 自动生成代码，请勿修改，如需扩展，请在 extendservice 目录下创建对应的 extend service implement ###
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>()<#if table.serviceInterface>, ${table.serviceName}</#if> {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}><#if table.serviceInterface> implements ${table.serviceName}</#if> {

}
</#if>
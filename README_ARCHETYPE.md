# 生成&使用脚手架模板
```bash
# 生成并本地安装 archetype
./mvnw archetype:create-from-project -Darchetype.properties=./archetype.properties
cd target/generated-sources/archetype 
../../../mvnw install
# 使用 archetype
mvn archetype:generate -DarchetypeGroupId=tech.nuoson -DarchetypeArtifactId=modulitharchetype -DgroupId=com.nuoson.app -DartifactId=new-app -DinteractiveMode=false

```
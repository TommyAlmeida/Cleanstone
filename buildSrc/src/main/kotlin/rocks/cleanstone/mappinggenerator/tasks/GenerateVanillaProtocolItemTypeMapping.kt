package rocks.cleanstone.mappinggenerator.tasks

import com.squareup.javapoet.*
import org.gradle.api.tasks.TaskAction
import rocks.cleanstone.mappinggenerator.util.ItemReportTask
import javax.lang.model.element.Modifier

open class GenerateVanillaProtocolItemTypeMapping : ItemReportTask() {

    @TaskAction
    fun generateVanillaItemMappings() {
        val itemList = getSortedItemList()

        val mapping = TypeSpec.classBuilder("ProtocolItemTypeMapping_v${getVersionString()}")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSpec.builder(ClassName.get("org.springframework.stereotype", "Component"))
                                .addMember("value", "\$S", "protocolItemTypeMapping_v${getVersionString()}")
                                .build()
                )
                .superclass(ParameterizedTypeName.get(
                        ClassName.get("rocks.cleanstone.game.material.item.mapping", "SimpleItemTypeMapping"),
                        ClassName.get(Integer::class.java)
                ))
                .addJavadoc("Maps Cleanstone's internal item types to the Minecraft client's format\n" +
                        "!! GENERATED FILE. This file was generated by gradle. !!")

        val vanillaItemType = ClassName.get("rocks.cleanstone.endpoint.minecraft.item", "VanillaItemType")
        val constructor = MethodSpec.constructorBuilder()
                .addStatement("super(\$T.STONE)", vanillaItemType)

        itemList.forEach {
            constructor.addStatement("setID(\$T.\$L, \$L)", vanillaItemType, it.getEnumName(), it.protocolID)
        }

        mapping.addMethod(constructor.build())

        JavaFile.builder("rocks.cleanstone.endpoint.minecraft.java.v${getVersionString()}.net.protocol", mapping.build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build()
                .writeTo(srcRoot.toPath())
    }


}
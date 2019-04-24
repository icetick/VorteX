package alex.orobinsk.codegeneration;

import alex.orobinsk.annotation.ModelBuilder
import com.google.auto.service.AutoService
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(BuilderGenerator.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class BuilderGenerator: AbstractProcessor() {
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ModelBuilder::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    private fun generateClass(classElement: Element){
        with(classElement) {
            val className = simpleName.toString()
            val pack = processingEnv.elementUtils.getPackageOf(this).toString()
            val variables = ElementFilter.fieldsIn(enclosedElements)

            val fileName = "${className}Builder"
            val fileContent = ClassBuilder(fileName, className, pack, variables).getContent()

            val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
            val file = File(kaptKotlinGeneratedDir, "$fileName.kt")

            file.writeText(fileContent)
        }
    }

    override fun process(p0: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        (roundEnvironment?.getElementsAnnotatedWith(ModelBuilder::class.java) as Iterable<Element>).forEach {
            generateClass(it)
        }
        return true
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}

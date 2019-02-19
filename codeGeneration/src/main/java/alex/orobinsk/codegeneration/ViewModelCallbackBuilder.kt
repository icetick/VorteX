package alex.orobinsk.codegeneration

import com.squareup.kotlinpoet.asTypeName
import java.lang.Exception
import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement

class ViewModelCallbackBuilder(
    fileName: String,
    className: String,
    packageName:String,
    variables: MutableList<out Element>){

    private val contentTemplate = """
package $packageName

import $packageName.ObservableCallback
import androidx.lifecycle.LifecycleOwner

interface $fileName(lifecycleOwner: LifecycleOwner): ObservableCallback {
${getFieldSetters(variables)}
}""".trimIndent()

    private fun getConstructorVariables(variables: MutableList<out Element>): String {
        val constructorVariables = StringBuilder()
        variables.forEachIndexed { index, it ->
            it as VariableElement
            constructorVariables.append("var " +
                    it.simpleName.toString() + ":" +
                    tryGetKClass(it) + "? = null" +
                    (if(index!=variables.lastIndex) ", " else ")"))
        }
        return constructorVariables.toString()
    }

    private fun getRequiredImports(variables: MutableList<out Element>): String {
        val requiredImports = StringBuilder()
        variables.forEachIndexed { index, it ->
            it as VariableElement
            var import = "import " + it.asType().asTypeName() + "\n"
            if(requiredImports.contains(import))
                requiredImports.append(import)
        }
        return requiredImports.toString()
    }

    private fun getFieldSetters(variables: MutableList<out Element>): String {
        val fieldSetters = StringBuilder()

        variables.forEach {
            it as VariableElement
            fieldSetters.append(
                "   fun " + "observe"+ it.simpleName.toString() + "(" +
                        it.simpleName.toString()+ ": " +
                        tryGetKClass(it) +
                        ") = apply {" + "this."
                        + it.simpleName.toString()
                        + " = " + it.simpleName.toString() + "} \n"
            )
        }
        return fieldSetters.toString()
    }

    private fun getBuildFunction(variables: MutableList<out Element>): String {
        val buildGenerated = StringBuilder()
        variables.forEachIndexed { index, element -> if(index!=variables.lastIndex) buildGenerated.append(element.simpleName.toString() + " as "
                + tryGetKClass(element) + ", ")
        else buildGenerated.append(element.simpleName.toString() + " as "
                + tryGetKClass(element))  }
        return buildGenerated.toString()
    }

    private fun tryGetKClass(element: Element): String? {
        return try {
            Class.forName(element.asType().asTypeName().toString()).kotlin.simpleName
        } catch (ex: Exception) {
            element.asType().asTypeName().toString()
        }
    }

    fun getContent() : String{
        return contentTemplate
    }

}
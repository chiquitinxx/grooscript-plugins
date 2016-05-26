import org.grooscript.grails.bean.GrooscriptConverter

import static org.grooscript.grails.util.Util.getClassSource

description('Generate grooscript component classes') {
    usage 'grails generate-component [fullClassName]'
    flag name: 'fullClassName', description: 'The component class full name to generate (example: com.components.MyComponent)'
}

try {
    if (args.size() < 1) {
        throw new Exception('Must specify at least one component class (example: grails generate-component com.components.MyComponent)')
    }
    GrooscriptConverter converter = new GrooscriptConverter()
    args.each { String fullName ->
        String simpleName = fullName.split('\\.').last()
        String groovyCode = getClassSource(fullName)
        String conversionResult = converter.convertComponent(groovyCode)
        if (!resourcesDir.exists()) {
            resourcesDir.mkdirs()
        }
        String path = resourcesDir.path + org.grooscript.util.Util.SEP + simpleName + '.cs'
        new File(path).text = conversionResult
        console.addStatus("Component class generated ${path}")
    }
} catch (e) {
    error e.message, e
}
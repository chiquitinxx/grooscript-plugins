import org.grooscript.grails.bean.GrooscriptConverter

description('Generate grooscript remote domain classes') {
    usage 'grails generate-remote-domain [fullClassName]'
    flag name: 'fullClassName', description: 'The domain class full name to generate remote domain class file (example: com.library.Book)'
}

try {
    if (args.size() < 1) {
        throw new Exception('Must specify at least one domain class (example: grails generate-remote-domain com.library.Book)')
    }
    GrooscriptConverter converter = new GrooscriptConverter()
    args.each { String fullName ->
        String simpleName = fullName.split('\\.').last()
        String conversionResult = converter.convertRemoteDomainClass(fullName)
        if (!resourcesDir.exists()) {
            resourcesDir.mkdirs()
        }
        String path = resourcesDir.path + System.getProperty('file.separator') + simpleName + '.gs'
        new File(path).text = conversionResult
        console.addStatus("Remote domain class generated ${path}")
    }
} catch (e) {
    error e.message, e
}
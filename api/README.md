# Knot.x Template Engine API

## How to create custom Template Engine strategy
All you need to do is simply implement 2 interfaces from the `knotx-template-engine-api`: 
- [`TemplateEngineFactory`](https://github.com/Knotx/knotx-template-engine/blob/master/api/src/main/java/io/knotx/te/api/TemplateEngineFactory.java)
- [`TemplateEngine`](https://github.com/Knotx/knotx-template-engine/blob/master/api/src/main/java/io/knotx/te/api/TemplateEngine.java).

and declare `META-INF/services/io.knotx.te.api.TemplateEngineFactory` on the classpath with your 
`TemplateEngineFactory` implementation.

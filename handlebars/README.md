# Knot.x Template Engine Handlebars
This section describes Handlebars template engine strategy that is default implementation of the
template engine used in Knot.x examples. 


## How does it work
Template Engine Handlebars uses 
[Handlebars Java port](https://github.com/jknack/handlebars.java) to compile and evaluate templates.
Please refer to its documentation for any details.
Additionally, Knot.x Template Engine Handlebars have built-in Guava in-memory cache for compiled HBS
Templates. It's key is computed basing on the `cacheKeyAlgorithm` defined in the configuration
(the default is `MD5` of the Fragment's `body`).

## How to configure
For all configuration fields and their defaults consult [HandlebarsEngineOptions](https://github.com/Knotx/knotx-template-engine/blob/master/handlebars/docs/asciidoc/dataobjects.adoc)

### Interpolation symbol
By default, the Handlebars engine uses `{{` and `}}` symbols as tag delimiters.
However, while Knot.x can be used to generate markup on the server side, the very same page might 
also contain templates intended for client-side processing. 
This is often the case when frameworks like Angular.js or Vue.js are used.
To avoid conflicts between Mustache templates to be executed server-side and ones evaluated 
on the client side, a Knot.x Handlebars TE introduces two configuration parameters that enable 
you to configure custom symbols to be used in Knot.x snippets.

E.g.:
In order to use different symbols as below
```html
<div class="col-md-4">
  <h2>Snippet1 - {<_result.message>}</h2>
</div>
```
You can reconfigure an engine as follows in the handlebars engine entry section:
```hocon
  {
    name = handlebars
    config = {
      cacheSize = 1000
      startDelimiter = "{["
      endDelimiter = "]}"
    }
  }
```

## Handlebars helpers and features
JKnack Handlebars port for java comes with [build-in helpers](https://github.com/jknack/handlebars.java#helpers) (note that you need to register some of them because they are not added by default).
Also, there are helpers created by Knot.x community in the [Knot.x Handlebars Extension](https://github.com/Knotx/knotx-handlebars-extension) repository.

## Extending handlebars with custom helpers

If the list of available handlebars helpers is not enough, you can easily extend it. To do this the 
following actions should be undertaken:

1. Use `io.knotx:knotx-template-engine-handlebars` module as dependency
2. Create a class implementing [`io.knotx.te.handlebars.CustomHandlebarsHelper`](https://github.com/Knotx/knotx-template-engine/blob/master/handlebars/src/main/java/io/knotx/te/handlebars/CustomHandlebarsHelper.java) interface. 
This interface extends [com.github.jknack.handlebars.Helper](https://jknack.github.io/handlebars.java/helpers.html)
3. Register the implementation as a service in the JAR file containing the implementation
    * Create a configuration file called `META-INF/services/io.knotx.te.handlebars.CustomHandlebarsHelper` 
    in the same project as your implementation class
    * Paste a fully qualified name of the implementation class inside the configuration file. If you're 
    providing multiple helpers in a single JAR, you can list them in new lines (one name per line is allowed) 
    * Make sure the configuration file is part of the JAR file containing the implementation class(es)
3. Run Knot.x with the JAR file in the classpath

### Example extension

See [acme-handlebars-ext](https://github.com/Knotx/knotx-example-project/tree/master/acme-handlebars-ext)
in the [`knotx-example-project`](https://github.com/Knotx/knotx-example-project).

This module contains an example custom Handlebars helper - `BoldHelper`.


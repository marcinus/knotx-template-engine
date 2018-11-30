# Knotx Template Engine Knot
Knotx Template Engine is module responsible for processing template and the data using chosen
template engine strategy.

## How does it work?
Template Engine filters [Fragments](https://github.com/Cognifide/knotx/wiki/Splitter) containing 
`te` in the `data-knotx-knots` attribute. Then for each Fragment it merges Fragment Content (snippet) 
with data from Fragment Context (for example data from external services or form submission response)
using chosen template engine strategy.

> Please note that example below uses Handlebars to process the markup. Read more about it below.

*Fragment Content*
```html
<knotx:snippet knots="services,te" databridge-name="first-service" type="text/knotx-snippet">
<div class="col-md-4">
  <h2>Snippet1 - {{_result.message}}</h2>
  <div>Snippet1 - {{_result.body.a}}</div>
  <div>Success! Status code : {{_response.statusCode}}</div>
</div>
</knotx:snippet>
```

*Fragment Context*
```json
{
  "_result": {
    "message":"this is webservice no. 1",
    "body": {
      "a": "message a"
    }
  },
  "_response": {
    "statusCode":"200"
  }
}
```

Template Engine Knot uses data from Fragment Context and applies it to Fragment Content:
```html
<div class="col-md-4">
  <h2>Snippet1 - this is webservice no. 1</h2>
  <div>Snippet1 - message a</div>
  <div>Success! Status code : 200</div>
</div>
```
Finally Fragment Content is replaced with merged result.

### Template Engine Strategy

Each snippet can specify one template engine strategy by defining `te-strategy` attribute in the
snippet e.g.:

```html
<knotx:snippet knots="te"
               te-strategy="acme"
               type="text/knotx-snippet">
  Some markup that te strategy can process...
</knotx:snippet>
```

Template Engine configuration enables to define `defaultEngine` that will be applied to each snippet
when no `te-strategy` is defined.

### Configuration
See the [configuration docs](https://github.com/Knotx/knotx-template-engine/blob/master/core/documentation/.dataobjects/core-dataobjects.adoc).

Example configuration is available in the [conf](https://github.com/Knotx/knotx-template-engine/blob/master/conf/includes/templateEngine.conf)
section of this module.

## Handlebars TE Strategy
This section describes Handlebars template engine strategy that is default implementation of the
template engine used in Knot.x examples. It uses 
[Handlebars Java port](https://github.com/jknack/handlebars.java) to compile and evaluate templates.

### Configuration
#### Interpolation symbol
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

### How to configure?
For all configuration fields and their defaults consult [HandlebarsEngineOptions](https://github.com/Knotx/knotx-template-engine/blob/master/handlebars/documentation/.dataobjects/core-dataobjects.adoc)

### Extending handlebars with custom helpers

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

#### Example extension

See [acme-handlebars-ext](https://github.com/Knotx/knotx-example-project/tree/master/acme-handlebars-ext)
in the [`knotx-example-project`](https://github.com/Knotx/knotx-example-project).

This module contains an example custom Handlebars helper - `BoldHelper`.

## How to extend?
// ToDo

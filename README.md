# Template Engine
Knot.x Template Engine module is a [Knot](https://github.com/Knotx/knotx-fragments-handler/tree/master/api#knot)
responsible for processing [Fragment's](https://github.com/Knotx/knotx-fragment-api#knotx-fragment-api)
`body` (treating it as a Template) and the data from Fragment's `payload` using chosen
template engine strategy.

## How does it work
Template Engine reads [Fragment's](https://github.com/Knotx/knotx-fragment-api#knotx-fragment-api)
`body` and treats it as a Template. It also reads Fragment's `payload` and uses the data within it
to resolve placeholders from the Template. Finally it overwrites Fragment's `body` and returns it
in the [`FragmentResult`](https://github.com/Knotx/knotx-fragments-handler/blob/master/api/docs/asciidoc/dataobjects.adoc#FragmentResult)
together with Transition.

> Please note that example below uses Handlebars to process the markup. Read more about it below.
> You may read about it in the [Handlebars module docs](https://github.com/Knotx/knotx-template-engine/tree/master/handlebars)

*Fragment's body*
```html
  <div class="col-md-4">
    <h2>{{_result.title}}</h2>
    <p>{{_result.synopsis.short}}</p>
    <div>Success! Status code: {{_response.statusCode}}</div>
  </div>
```

*Fragment's payload*
```json
{
  "_result": {
    "title":"Knot.x in Action",
    "synopsis": {
      "short": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc vel enim ac augue egestas rutrum non eget libero."
    }
  },
  "_response": {
    "statusCode":"200"
  }
}
```

*Fragment's body after processing*
```html
<div class="col-md-4">
  <h2>Knot.x in Action</h2>
  <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc vel enim ac augue egestas rutrum non eget libero.</p>
  <div>Success! Status code: 200</div>
</div>
```

### Template Engine Strategy

Each snippet can specify one template engine strategy by defining `te-strategy` attribute in the
snippet e.g.:

```html
<knotx:snippet knots="te"
               te-strategy="acme"
               type="text/knotx-snippet">
  Some markup that acme strategy can process...
</knotx:snippet>
```

Template Engine configuration enables to define `defaultEngine` that will be applied to each snippet
when no `te-strategy` is defined.

## How to use
> Please note that example below uses Handlebars to process the markup. Read more about it below.
> You may read about it in the [Handlebars module docs](https://github.com/Knotx/knotx-template-engine/tree/master/handlebars)

Define a module that creates `io.knotx.te.core.TemplateEngineKnot` instance.

```hocon
modules {
  myTemplateEngine = "io.knotx.te.core.TemplateEngineKnot"
}
```

Configure it to listen on some `address` and other things:
```hocon
config.myTemplateEngine {
  address = my.template.engine.eventbus.address
  defaultEngine = handlebars
}
```
See the [configuration docs](https://github.com/Knotx/knotx-template-engine/blob/master/core/docs/asciidoc/dataobjects.adoc)
for detailed configuration options.

In the [Fragment's Handler actions section](https://github.com/Knotx/knotx-fragments-handler/tree/master/core#actions) 
define a Template Engine Knot Action using `knot` factory.
```hocon
actions {
  te-hbs {
    factory = "knot"
    config {
      address = "my.template.engine.eventbus.address"
    }
  }
}
```

Now you may use it in Fragment's Tasks.

Example configuration is available in the [conf](https://github.com/Knotx/knotx-template-engine/blob/master/conf)
section of this module.

## Handlebars Template Engine Strategy
Currently this repository delivers `handlebars` TE strategy implementation.
You may read more bout it in the [module docs](https://github.com/Knotx/knotx-template-engine/tree/master/handlebars).

### How to create a custom Template Engine Strategy
Read more about it in the [API module docs](https://github.com/Knotx/knotx-template-engine/tree/master/api).

You can find example (and very simple) TE strategy implementation in the 
[Knot.x example project - acme te](https://github.com/Knotx/knotx-example-project/tree/master/acme-template-engine).

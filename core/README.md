# Knot.x Template Engine Core

This module delivers a [Knot](https://github.com/Knotx/knotx-fragments-handler/tree/master/api#knot)
responsible for processing [Fragment's](https://github.com/Knotx/knotx-fragment-api#knotx-fragment-api)
`body` (treating it as a Template) and the data from Fragment's `payload` using chosen
template engine strategy.

## Template Engine Strategy
Each Template Engine Knot defines only a single `engine`:
```hocon
config.myTemplateEngine {
  address = my.template.engine.eventbus.address
  factory = myTeEngineStrategy
}
```
See the [configuration docs](https://github.com/Knotx/knotx-template-engine/blob/master/core/docs/asciidoc/dataobjects.adoc)
for detailed configuration options.

In order to have multiple Template Engine Strategies defined create multiple instances of `io.knotx.te.core.TemplateEngineKnot`
and configure each to have its own TE Strategy, e.g.:

```hocon
modules {
  myTemplateEngine = "io.knotx.te.core.TemplateEngineKnot"
  handlebars = "io.knotx.te.core.TemplateEngineKnot"
}
```

Configure it to listen on some `address` and other things:
```hocon
config.myTemplateEngine {
  address = my.template.engine.eventbus.address
  factory = myCustomEngine
}
config.handlebars {
   address = io.knotx.te.handlebars
   factory = handlebars
 }

```

In the [Fragment's Handler actions section](https://github.com/Knotx/knotx-fragments-handler/tree/master/core#actions) 
define a separate Action for each TE Knot:
```hocon
actions {
  te-custom {
    factory = knot
    config {
      address = my.template.engine.eventbus.address
    }
  }
  te-hbs {
    factory = knot
    config {
      address = io.knotx.te.handlebars
    }
  }
}
```

# Chatty
[![Javadoc](https://img.shields.io/badge/Javadoc-Online-green)](https://thijzert123.github.io/chatty/javadoc/)  [![Maven Package](https://github.com/Thijzert123/chatty/actions/workflows/maven-package.yml/badge.svg)](https://github.com/Thijzert123/chatty/actions/workflows/maven-package.yml)

Chatty is a Java chat platform that's still work in progress. Help is appreciated, so feel free to open an issue or pull request.

## Compiling
Build jar files:
```bash
mvn package
```
Generate Javadocs locally (they will be available at `target/reports/apidocs/index.html`):
```bash
mvn javadoc:javadoc
```

## Contributing
Any help is greatly appreciated. But if you open a pull request, you have to follow the coding guide.

### Coding Guide
Always add `final` to as much as possible fields.

#### Naming
- Classes: `ClassName`
- Methods: `methodName`
- Global class variables (everything but static final): `variableName_`
- Class variables (static final, except serialVersionUID): `VARIABLE_NAME`
- Local variables: `variableName`

#### Definition order
- Classes: `(public) (final) class ClassName`
- Variables: `(public) (static) (final) <type> variableName`
- Methods: `(public) (static) (void/<type>) methodName`

#### Javadoc
- Add Javadoc to all:
  - Classes and nested classes
  - Methods (of all visibilities)
  - Interfaces
  - Enums
  - Class fields (except if they are private)
- Make use of these tags and use more if necessary:
  - `@see`
  - `@use`
  - `@author`
  - `@deprecated`
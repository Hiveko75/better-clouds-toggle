# Better Clouds Toggle

A small client-side Fabric add-on for [Better Clouds](https://github.com/Qendolin/better-clouds)
that adds a key binding to switch Better Clouds on and off, the same as its **Enable** option.

- Bind it under *Options → Controls → Key Binds → Better Clouds → Toggle Better Clouds* (unbound by default).
- Takes effect immediately and is saved to Better Clouds' own config. While off, vanilla clouds are shown.
- Better Clouds is not modified, bundled or compiled against; this mod only flips its existing setting.

Requires Better Clouds and Fabric API.

| Jar | Minecraft |
|---|---|
| `better-clouds-toggle-<version>+mc26.1.2-26.3.jar` | 26.1.2, 26.2, 26.3 |
| `better-clouds-toggle-<version>+mc1.21.11.jar` | 1.21.11 |

Building: `./gradlew build` (26.x jar) and `./gradlew -p mc1.21.11 build` (1.21.11 jar).
CI also compiles the 26.x sources against each 26.x version and checks the bytecode is identical.

License: MIT. Better Clouds is MPL-2.0 by Qendolin.

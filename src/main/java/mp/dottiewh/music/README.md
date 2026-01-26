## Documentación de /du music

#### /du music
- #### Soporta customización.
- #### Se maneja por partes, parecido a un FlStudio.
- ## Su uso
  - #### /du music list
  - #### /du music play (Nombre de la canción) _[Jugadores]_ _[Loop]_
  - #### /du music stop _[Jugadores]_

### En la carpeta musics
### CADA MÚSICA DEBE TENER SU PROPIO ARCHIVO.
### EL NOMBRE DE LA MÚSICA **NO** PUEDE TENER ESPACIOS. (será su id)
#### _Usando la canción "demo".yml de ejemplo:_
```
demo:
    DisplayMaterial: YELLOW_DYE #What it shows in the music menu
    Structure:
        Part0:
            ticks_to_continue: 40 #ticks before part "ends"
            section_list:
                - "bass"
        Part1:
            ticks_to_continue: 480 #ticks before part "ends"
            section_list:
                - "bass"
                - "drums"
                - "chords"
                - "solo"
        Part2:
            ticks_to_continue: 240 #then it'll continue with next part.
            section_list:
                - "bass"
                - "drums"
                - "chords"
        Part3:
            ticks_to_continue: 160 # IN CASE IT DOESN'T MATCH CORRECTLY
            section_list:
                - "bass"
                - "chords"
        Part4:
            ticks_to_continue: 40 # It'll stop some sections a little before part ends.
            section_list:
                - "bass"
    Sections:
        bass:
            - "wait;2" # in ticks
            - "BLOCK.NOTE_BLOCK.BASS;10;1.189207"
            - "wait;4" # in ticks
            - "BLOCK.NOTE_BLOCK.BASS;10;1.781797"
            - "wait;6" # in ticks
            - "BLOCK.NOTE_BLOCK.BASS;10;1.059463"
            - "wait;4" # in ticks
            - "BLOCK.NOTE_BLOCK.BASS;10;1.587401"
            - "wait;6" # in ticks
            - "BLOCK.NOTE_BLOCK.BASS;10;0.943874"
            - "wait;4" # in ticks
            - "BLOCK.NOTE_BLOCK.BASS;10;1.414214"
            - "wait;6" # in ticks
            - "BLOCK.NOTE_BLOCK.BASS;10;0.890899"
            - "wait;4" # in ticks
            - "BLOCK.NOTE_BLOCK.BASS;10;1.334840"
            - "wait;4"
        drums:
            - "wait;2"
            - "BLOCK.NOTE_BLOCK.BASEDRUM;10;0.5"
            - "wait;6"
            - "BLOCK.NOTE_BLOCK.SNARE;10;0.5"
            - "wait;4"
            - "BLOCK.NOTE_BLOCK.BASEDRUM;10;0.5"
            - "wait;4"
            - "BLOCK.NOTE_BLOCK.SNARE;10;0.5"
            - "wait;4"
        chords:
            - "wait;2"
            - "BLOCK.NOTE_BLOCK.PLING;2;1.189207"
            - "BLOCK.NOTE_BLOCK.PLING;2;1.498307"
            - "BLOCK.NOTE_BLOCK.PLING;2;1.781797"
            - "wait;10"
            - "BLOCK.NOTE_BLOCK.PLING;2;1.059463"
            - "BLOCK.NOTE_BLOCK.PLING;2;1.334840"
            - "BLOCK.NOTE_BLOCK.PLING;2;1.587401"
            - "wait;10"
            - "BLOCK.NOTE_BLOCK.PLING;2;0.943874"
            - "BLOCK.NOTE_BLOCK.PLING;2;1.189207"
            - "BLOCK.NOTE_BLOCK.PLING;2;1.414214"
            - "wait;10"
            - "BLOCK.NOTE_BLOCK.PLING;2;0.890899"
            - "BLOCK.NOTE_BLOCK.PLING;2;1.059463"
            - "BLOCK.NOTE_BLOCK.PLING;2;1.334840"
            - "BLOCK.NOTE_BLOCK.PLING;2;1.587401"
            - "wait;8"
        solo:
            - "wait;2"
            - "BLOCK.NOTE_BLOCK.IRON_XYLOPHONE;20;1.414214"
            - "wait;5"
            - "BLOCK.NOTE_BLOCK.IRON_XYLOPHONE;20;1.587401"
            - "wait;5"
            - "BLOCK.NOTE_BLOCK.IRON_XYLOPHONE;20;1.781797"
            - "wait;5"
            - "BLOCK.NOTE_BLOCK.IRON_XYLOPHONE;20;1.587401"
            - "wait;3"
            - "BLOCK.NOTE_BLOCK.IRON_XYLOPHONE;20;1.414214"
            - "wait;12"
            - "BLOCK.NOTE_BLOCK.IRON_XYLOPHONE;20;1.189207"
            - "wait;10"
            #
            - "BLOCK.NOTE_BLOCK.IRON_XYLOPHONE;20;1.414214"
            - "wait;10"
            - "BLOCK.NOTE_BLOCK.IRON_XYLOPHONE;20;1.587401"
            - "wait;10"
            - "BLOCK.NOTE_BLOCK.IRON_XYLOPHONE;20;1.781797"
            - "wait;10"
            - "BLOCK.NOTE_BLOCK.IRON_XYLOPHONE;20;1.587401"
            - "wait;8"
 ```
## DisplayMaterial
- #### Sería el material con el que se muestra tu canción en el _/du music menu_.
- #### Puedes usar cualquier material de [Materials - Spigot](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html).
## Structure:
- ### Part1: _| Part2, Part3..._ 
  - #### Vendría a ser un nombre X para identificar la estructura de tu canción.
- ### ticks_to_continue:
  - #### Son los ticks que va a durar tu parte.
  - #### Las secciones de la parte DEBEN sincronizarse JUSTAMENTE al final de esos ticks para sonar bien.
  - #### Imagina tienes una sección A que dura 64 ticks, y una B que dura 128 ticks
    - #### Entonces ticks_to_continue: podría ser 128, 256, 384, 512...
  - #### Imagina tienes una sección A que dura 64 ticks, una B que dura 128 ticks, una C que dura 256 ticks
      - #### Entonces ticks_to_continue: podría ser 256, 512, 768, 1024...
- ### section_list:
  - #### Aquí iría el nombre de las secciones (definidas después en Sections) que irán a sonar durante tal parte.

## Sections:
- ### Bass | Drums | Chords | solo...
  - #### Es un nombre X (puedes ponerle el que quieras), con el cual se puede identificar la sección en _section_list_.
- ### "BLOCK.NOTE_BLOCK.BASS;10;1.189207"
  - #### BLOCK.NOTE_BLOCK.BASS
    - #### Sería el ID del sonido que sonará.
    - #### Puede ser cualquiera de esta lista. [Sonidos MC](https://minecraftsounds.com/)
  - #### 10
    - #### Sería el volumen de la nota o sonido.
    - #### Se recomienda el volumen 10 como genérico y 2 para acordes.
  - #### 1.189207
    - #### Sería el pitch del sonido.
    - #### En caso de estar intentando importar note blocks de una build, puedes usar la wiki para saber el pitch [Note Block - MC Wiki](https://minecraft.wiki/w/Note_Block)
  - ### Todos estos conceptos van separados por un **;**
- ### "wait;2"
  - #### Es la forma de determinar el número de ticks que se demorará antes de pasar al siguiente sonido.
  - #### En caso de estar "importando" una build de un mundo, ten en cuenta que debes multiplicar los ticks del repeater * 2. _(1 tick redstone = 2 ticks normales)_
  - ### Si sumas todos los ticks de los waits, puedes sacar el tiempo total de X sección.
## Documentación de /du item y config.

- #### /du item
    - #### Soporta nombre custom.
    - #### Soporta lore custom.
    - #### Soporta encantamientos.
    - #### Soporta atributos.
    - #### Soporta atributos de comida.
    - #### Soporta unbreakable.
      - ## Su uso:
          - #### /du item save (Nombre del item)
          - #### /du item get (Nombre del item) _(Opcional: Cantidad)_
          - #### /du item give (Nombre del item) (Jugador) _(Opcional: Cantidad)_
          - #### /du item delete (Nombre del item)
          - #### /du item list
### En Items.yml
#### _un ejemplo sería:_ 
####
    Items:
      DiamanteMagico:
        Material: DIAMOND
        Name: "&9&lDiamante &e&lIncreíble"
        Lore:
        - '&7&lEsto es mi linea 1!'
        - '&d&l&nMientras que esta es mi linea 2'
        Enchants:
            knockback: 10
            vanishing_curse: 1
        Attributes:
            armor:
                value: 5.0
                operation: ADD_NUMBER
                slot: offhand
            jump_strength:
                value: 1.0
                operation: MULTIPLY_SCALAR_1
                slot: mainhand
##### **IMPORTANTE**: _Cosas como el material, los enchantamientos, los atributos y sus ids, son no case-sensitive_
- #### **DiamanteMagico:** Es el id con el que se registra el objeto.
- #### **Material:** Es cualquier material que esté en: [Spigot - MATERIAL](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)
- #### **Name:** Recomendando poner entre " ", es el nombre con el que se ve en el juego, acepta codigos de colores básicos.
- #### **Lore:** Cada lista (-), es una linea independiente. Recomendado poner entre " ", y acepta codigos de colores básicos.
- #### **Enchants:** Cada valor que derive de este, es un encantamiento. Valores aceptados: [Spigot - ENCHANTMENT](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html)
  - #### Formato: "_(Encantamiento)_: _(Nivel)_"
- #### **Attributes:** Se divide en atributos registrados en [Spigot - ATTRIBUTE](https://jd.papermc.io/paper/1.21.8/org/bukkit/attribute/Attribute.html), con la estructura de:
    - #### **value:** Un valor numérico.
    - #### **operation:** Tipo de operación con el que se tratará el value, tiene que estar registrado en: [Spigot - ATTRIBUTE.OPERATION](https://jd.papermc.io/paper/1.21.8/org/bukkit/attribute/AttributeModifier.Operation.html)
    - #### **slot:** Representa donde hará efecto el atributo, puede ser cualquiera de: [Spigot - EQUIPMENTSLOTGROUP](https://jd.papermc.io/paper/1.21.8/org/bukkit/inventory/EquipmentSlotGroup.html)

#### _Otro ejemplo_
####
```
    Items:
      BreezeTrick:
          Material: BREEZE_ROD
          Glider: true
          Enchantment_glint_override: true
          Hide_tooltip: true
          Fire_resistant: true
          Enchantable: 200
          Jukebox_playable: minecraft:pigstep
          Model: resourcepack:model
          ToolTip_style: namespace:path
          UseCooldown:
            cooldown_group: key:value
            cooldown_seconds: 1.5
          Equippable:
            slot: HEAD
            equip_sound: BLOCK.AMETHYST_BLOCK.HIT
            asset_id: resourcepack:model #Uses a resource pack model, in a namespacedkey way
            dispensable: true
            equip_on_interact: true #for mobs
            swappable: false
            damage_on_hurt: false
          Tool:
            damage_per_block: 2
            default_mining_speed: 3.5
            rules:
              rule_1:
                correct_for_drops: true
                speed: 25
                applies:
                - "NETHERITE_BLOCK"
                - "DIAMOND_BLOCK"
                - "GOLD_BLOCK"
              rule_2:
                correct_for_drops: false
                speed: 10.5
                applies:
                - "OBSIDIAN"
                - "BEACON"
```           
#### **Glider** _(true/false)_
- #### Define si actuará como si fuera una elytra.
#### **Enchantment_glint_override** _(true/false)_
- #### Define si tendrá el glow de un encantamiento.
#### **Fire_resistant** _(true/false)_
- #### Define si es resistente al fuego.
#### **Enchantable** _(entero)_
- #### Entre mayor encantabilidad, mejores encantamientos le saldrán.
#### **Jukebox_playable**
- #### Define si se reproducirá una canción al interactuar con un jukebox.
- #### Sigue con el formato key:value
#### **Model**
- #### El modelo que usará el objeto, apto para resourcepacks.
- #### Usa el formato key:value
#### **ToolTip_style**
- #### El estilo o fondo que usará el objeto en el background/frame, apto para resourcepacks.
- #### Usa el formato key:value
#### **UseCooldown**
- #### cooldown_group
  - #### Los items que estén en el mismo grupo, compartiran el cooldown.
- #### cooldown_seconds
  - #### Tiempo en segundos que durará el cooldown.
### Equippable
- #### slot
  - #### Cualquier slot de [Spigot - EquipmentSlot](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/EquipmentSlot.html).
- #### equip_sound
  - #### Cualquier sonido de [MC Sounds - Sounds](https://minecraftsounds.com/).
- #### asset_id
  - #### Modelo que se verá, apto para resource packs
  - #### Usa el formato key:value
- #### dispensable _(true/false)_
  - #### Define si al usar un dispensador, te equipará el item.
- #### equip_on_interact _(true/false)_
  - #### Define si le puedes equipar el item a los mobs al interactuar con ellos.
- #### swappable _(true/false)_
  - #### Define si puedes equiparte el item al interactuar con él.
- #### damage_on_hurt _(true/false)_
  - #### Define si se le gastará durabilidad al tomar daño.
### Tool
  - #### damage_per_block
    - #### Durabilidad que se le gastará al item por defecto al romper un bloque.
  - #### default_mining_speed
    - #### Velocidad por defecto al romper un bloque
    - #### Es un multiplicador, es decir, un "2" romperá los bloques el doble de rápido. (1=default)
  ### rules
  - #### rule_1 (Es un nombre subjetivo para destacar una regla)
    - #### correct_for_drops (true/false)
      - #### Define si te darán los items al romper tales bloques.
    - #### speed
      - #### Actua de la misma forma que default_mining_speed, pero con tales bloques.
    - #### applies
      - #### Define la lista de bloques a los que les afecta la data.
      - #### Puede ser cualquiera de [Spigot - Materials](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)

#
#### _Otro ejemplo_
#### 
    Items:
      Sospechoso:
          Material: COOKED_BEEF
          Food:
            always_eatable: true
            nutrition: 2
            saturation: 2.0
          Unbreakable: true
- #### **Food:** Se divide en una lista de atributos derivados de. Este apartado solo es recomendado usarse con comidas (o consumibles).
    - #### **always_eatable:** Acepta true o false. Determina si siempre se puede comer siempre o no.
    - #### **nutrition:** Un valor numérico de cuanto alimentará.
    - #### **saturation:** Un valor numérico de cuanta saturación dará.
- #### **Unbreakable:** Acepta true o false, en este ejemplo no sirve al ser filete, pero para herramientas sí sirve.

#### _Otro ejemplo_
####
    Items:
      sus:
        Material: MINECART
        Max_stack_size: 25
        Consumable:
          seconds: 2.0
          animation: SPYGLASS
          sound: ENTITY.GENERIC.EAT
          consumeparticles: true
          ClearAllStatusEffects:
            status: false
          RemoveStatusEffects:
            effects:
            - STRENGTH
            - JUMP_BOOST
            - SPEED
          ApplyStatusEffects:
            probability: 1.0
            JUMP_BOOST:
              duration: 100
              amplifier: 2
            SPEED:
              duration: 100
              amplifier: 2
          TeleportRandomly:
            diameter: 10.0
          PlaySound:
            sound: ENTITY.WITHER.SPAWN
#### **Max_stack_size:** Marca el número máximo de items que puedes llevar. (Min 1, Max: 99)
### Resumen de Consumable:
#### **seconds:** Marca los segundos que se demora en consumir el objeto.
#### **animation:** La animación que se va a hacer en consumir, acepta todas las de [Spigot - ConsumableAnimations](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/meta/components/consumable/ConsumableComponent.Animation.html).
#### **sound:** El sonido al consumir, acepta: [Spigot - Sounds](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html).
#### **consumeparticles:** true/false, si habrá particulas MIENTRAS se consume.
#### **ClearAllStatusEffects:**, poner el status en true o false, si quieres que funcione como la leche.
#### **RemoveStatusEffects:** Poner en effects:, una lista de los efectos que podrías querer que remueva, acepta: [Spigot - Effects](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html).
#### **ApplyStatusEffects:**
- **probability:**, poner en un número del 0-1, la probabilidad de que se activen los efectos (1 siempre).
- _**efecto de poción** [Spigot - Effects](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html)_
  - **duration:** Duración en ticks.
  - **amplifier:** El nivel.
#### **TeleportRandomly:** Poner en diameter: un rango de cuando te puede tpear.
#### **PlaySound:** Sonido que se hará AL CONSUMIR, acepta: [Spigot - Sounds](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html).
#
#### _Otro ejemplo_
```
Items:
  test:
    Material: DIAMOND_SWORD
    Name: '&9&lWow.'
    Lore:
      - '&cTest!'
      - '&eLine 2!'
    Enchants:
      sharpness: 5
      knockback: 2
    CustomData:
      onAttack:
        effectapply: SLOWNESS;100;1
        particles: DUST;0;10
```
### Resumen de CustomData
#### onAttack
- #### effectapply:
  - #### Efecto a aplicar
  - #### Usa el formato de ID;DURATION;AMPLIFIER | _(duration en ticks)_
  - #### O también usa el formato ID;DURATION;AMPLIFIER;AMBIENT;PARTICLES;ICON | _(ambient, particles y icon son un boolean, ósea true or false)_
  - #### Cualquiera de [Spigot - Effects](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html).
#### particles:
  - #### Particulas a aplicar
  - #### Cualquiera de [Spigot - Particles](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html)
  - #### Sigue el formato de ID;offsetX;offsetY;offsetZ;speed;amount;force 
  - #### SI ES DUST
    - #### Sigue el formato de DUST;red;green;blue;offsetX;offsetY;offsetZ;size;amount;force
- #### SI ES TRAIL
  - #### Sigue el formato de TRAIL;red;green;blue;offsetX;offsetY;offsetZ;duration;amount;force
#
### **IMPORTANTE:** 
- #### _NO CAMBIAR MAYÚSCULAS DE (LITERALMENTE, NO ME REFIERO A SUS VALORES): "Material, Name, Lore, Enchants, Attributes, value, operation, slot, Food, always_eatable", "nutrition", "saturation", "seconds", "animation", "sound", "consumeparticles", etc_
- #### Básicamente, no jugar mucho con las mayúsculas donde NO debes introducir valores, y son de ESTRUCTURA.


## 30/08/2025
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
          - #### /du item delete _[del]_ (Nombre del item)
          - #### /du item list
### En Items.yml
#### _un ejemplo sería:_ 
####
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
##### **IMPORTANTE**: _Cosas como el material, los enchantamientos los atributos y sus ids, son no case-sensitive_
- #### **DiamanteMagico:** Es el id con el que se registra el objeto.
- #### **Material:** Es cualquier material que esté en: [Spigot - MATERIAL](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)
- #### **Name:** Recomendando poner entre "", es el nombre con el que se ve en el juego, acepta codigos de colores básicos.
- #### **Lore:** Cada lista (-), es una linea independiente. Recomendado poner entre "", y acepta codigos de colores básicos.
- #### **Enchants:** Cada valor que derive de este, es un encantamiento. Valores aceptados: [Spigot - ENCHANTMENT](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html)
- #### **Attributes:** Se divide en atributos registrados en [Spigot - ATTRIBUTE](https://jd.papermc.io/paper/1.21.8/org/bukkit/attribute/Attribute.html), con la estructura de:
    - #### **value:** Un valor numérico.
    - #### **operation:** Tipo de operación con el que se tratará el value, tiene que estar registrado en: [Spigot - ATTRIBUTE.OPERATION](https://jd.papermc.io/paper/1.21.8/org/bukkit/attribute/AttributeModifier.Operation.html)
    - #### **slot:** Representa donde hará efecto el atributo, puede ser cualquiera de: [Spigot - EQUIPMENTSLOTGROUP](https://jd.papermc.io/paper/1.21.8/org/bukkit/inventory/EquipmentSlotGroup.html)

#### _Otro ejemplo_
#### 
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

### **IMPORTANTE:** 
- #### _NO CAMBIAR MAYÚSCULAS DE (LITERALMENTE, NO ME REFIERO A SUS VALORES): "Material, Name, Lore, Enchants, Attributes, value, operation, slot, Food, always_eatable", "nutrition", "saturation"_

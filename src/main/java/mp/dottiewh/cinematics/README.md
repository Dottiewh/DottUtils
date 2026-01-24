## Documentación de /du cinematic y config

- #### /du cinematic
    - #### Soporta delay entre keyframes customizables. (x⋲N / x>0)
    - #### Soporta titles de por medio
    - #### Tiene algunos problemas con chunks no lodeados, en consecuencia, también al cambiar de mundos.
        - ## Su uso:
            - #### /du cinematic delete (Nombre de la cinemática)
            - #### /du cinematic list
            - #### /du cinematic play _(Nombre del item)_ _(Jugador)_ _(NPCS)_
            - #### /du cinematic stop _(Jugador)_
            - #### /du cinematic record
              - #### /du cinematic record start (Nombre de la cinemática) _(Periodo)_
              - #### /du cinematic record stop
              - #### /du cinematic record pause
              - #### /du cinematic record resume
              
- ### Al grabar una cinemática, probablemente tenga una estructura como esta:
```
Period: 15
    Locations:
        - world;313.1674035483605;67.33651992104923;159.16521833800354;39.654404;14.399993
        - world;310.67089099453545;67.33651992104923;162.28872580624426;36.80438;14.699992
        - world;309.093647372127;67.33651992104923;168.34688034305907;47.754383;18.149984
        - world;307.9796250589729;67.33651992104923;176.60863953493774;90.65442;34.94998
        - world;303.41577718001264;67.33651992104923;181.15449204807433;178.10446;35.99999
        - world;298.18199586933696;67.33651992104923;178.57579334223848;-129.99551;37.05
        - world;297.8987340113565;67.33651992104923;172.15458027761193;-88.1456;36.749996
        - world;302.8237694306768;67.33651992104923;168.03910694710578;-14.645508;36.599995
        - world;304.2135038264339;67.33651992104923;171.19224215599675;37.254456;22.199978
        - end
```
### Period
- #### Vendria a ser el tiempo en ticks antes de tepear la camara al siguiente keyframe.
- #### **Tiene** que ser mayor a 0.
- #### Se recomienda un número no tan bajo para no spamear el yml, puesto que, al reproducir la animación NO se verá más fluído en la mayoría de los casos.

### Locations:
- #### world;313.16;67.33;159.16;39.65;14.4
  - #### Usa el formato de x;y;z;yaw;pitch
- #### end
  - #### Decalara el final de la cinemática.
- ### OPCIONES OPCIONALES Y MANUALES.
  - ##### Puedes editar el yml para agregarle funciones, ejemplos:
  - #### **title**
    - #### En medio de las ubicaciones guardadas, puedes insertar un title, usando el formato
    - #### "title;mainMsg;subtitle;fadeIn;stay;fadeOut" (fades y stay en ticks) (subtitle puede ser "null" para estar vacío)
    - #### Si quieres sincronizar varios titles, debes espaciarlos teniendo en cuenta que cada línea de **ubicación** equivale al period.
  - #### **Redirigir a otra cinemática**
      - #### En medio de las ubicaciones guardadas, puedes REDIRIGIR a otra cinemática.
      - #### El formato sería poner el _"Nombre de la cinemática"_.
      - #### Esto es una buena opción si necesitas diferentes ángulos sin transición.

## EJEMPLO FINAL CON LAS OPCIONES ADICIONALES
#### _Imaginemos que tenemos otra cinemática guardada con el nombre test_1_
```
Period: 15
    Locations:
        - title;Hola esto es un title;un subtitle;15;30;15
        - world;313.1674035483605;67.33651992104923;159.16521833800354;39.654404;14.399993
        - world;310.67089099453545;67.33651992104923;162.28872580624426;36.80438;14.699992
        - world;309.093647372127;67.33651992104923;168.34688034305907;47.754383;18.149984
        - world;307.9796250589729;67.33651992104923;176.60863953493774;90.65442;34.94998
        - title;Esto sería después de 60 ticks;null;0;15;0
        - world;303.41577718001264;67.33651992104923;181.15449204807433;178.10446;35.99999
        - title;Y esto después de 15 ticks;debería estar sincronizado en teoría;15;30;15
        - world;298.18199586933696;67.33651992104923;178.57579334223848;-129.99551;37.05
        - world;297.8987340113565;67.33651992104923;172.15458027761193;-88.1456;36.749996
        - world;302.8237694306768;67.33651992104923;168.03910694710578;-14.645508;36.599995
        - world;304.2135038264339;67.33651992104923;171.19224215599675;37.254456;22.199978
        - test_1
        - end # En este caso el end no tendría efecto al redirigir a test_1
```

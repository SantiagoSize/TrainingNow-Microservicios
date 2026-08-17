package com.tn.biblioteca.config;

import com.tn.biblioteca.model.Ejercicio;
import com.tn.biblioteca.repository.EjercicioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Seed de la biblioteca: 15 ejercicios con información didáctica completa
 * (ejecución paso a paso, consejos, errores comunes y volumen recomendado).
 * Los listados usan "|" como separador.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final EjercicioRepository repository;
    private final JdbcTemplate jdbcTemplate;

    /** Video único (tutorial general de técnica de gimnasio) asignado a los 15 ejercicios
     *  sembrados, a pedido de Santiago para simplificar la demo en vez de buscar un video
     *  específico por ejercicio. */
    private static final String VIDEO_URL_DEMO = "https://www.youtube.com/watch?v=iA7kjqfMzS4";

    /** Fotos reales (comprimidas a 800px, JPEG) que Santiago fue mandando ejercicio por
     *  ejercicio, en resources/ejercicios/. Se completan por nombre según van llegando;
     *  los ejercicios sin foto todavía quedan con imageUrl vacío hasta que se agreguen aquí. */
    private static final Map<String, String> FOTOS_EJERCICIOS = Map.ofEntries(
            Map.entry("Press de banca", "press_de_banca.jpg"),
            Map.entry("Press inclinado con mancuernas", "press_inclinado_mancuernas.jpg"),
            Map.entry("Aperturas con mancuernas", "aperturas_mancuernas.jpg"),
            Map.entry("Dominadas", "dominadas.jpg"),
            Map.entry("Remo con barra", "remo_con_barra.jpg"),
            Map.entry("Jalón al pecho", "jalon_al_pecho.jpg"),
            Map.entry("Sentadilla", "sentadilla.jpg"),
            Map.entry("Prensa de piernas", "prensa_de_piernas.jpg"),
            Map.entry("Peso muerto rumano", "peso_muerto_rumano.jpg"),
            Map.entry("Press militar", "press_militar.jpg"),
            Map.entry("Elevaciones laterales", "elevaciones_laterales.jpg"),
            Map.entry("Curl con barra", "curl_con_barra.jpg"),
            Map.entry("Fondos en paralelas", "fondos_en_paralelas.jpg"),
            Map.entry("Plancha", "plancha.jpg"),
            Map.entry("Crunch abdominal", "crunch_abdominal.jpg")
    );

    @Override
    public void run(String... args) {
        asegurarColumnaImagenAmplia();
        boolean yaSembrado = repository.count() > 0;

        if (!yaSembrado) repository.saveAll(List.of(

            // ==================== PECTORALES ====================
            Ejercicio.builder()
                .name("Press de banca")
                .category("Pectorales")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Ejercicio básico de empuje horizontal. Es el principal constructor de fuerza y masa para el pecho.")
                .muscles("Pectoral mayor, Tríceps, Deltoides anterior")
                .difficulty("INTERMEDIO")
                .equipment("Barra y banco plano")
                .instructions("Acuéstate en el banco con los ojos bajo la barra y los pies firmes en el suelo.|"
                        + "Agarra la barra un poco más ancho que los hombros, con las muñecas rectas.|"
                        + "Retrae las escápulas: junta los omóplatos y mantén el pecho alto.|"
                        + "Baja la barra de forma controlada hasta rozar la parte media del pecho (2-3 segundos).|"
                        + "Empuja hacia arriba extendiendo los codos sin bloquearlos por completo.|"
                        + "Mantén la tensión y repite sin rebotar la barra en el pecho.")
                .tips("Mantén los pies apoyados y el glúteo en contacto con el banco todo el tiempo.|"
                        + "Los codos deben ir a unos 45° del torso, no abiertos a 90°.|"
                        + "Inhala al bajar y exhala al empujar.|"
                        + "Usa un compañero o barras de seguridad si trabajas con carga alta.")
                .commonMistakes("Rebotar la barra en el pecho: quita tensión y puede lesionar el esternón.|"
                        + "Levantar los glúteos del banco para ayudarse con la carga.|"
                        + "Abrir los codos en exceso, lo que estresa el hombro.|"
                        + "Bajar la barra al cuello en lugar de a la línea del pecho.")
                .recommendedSets(4).recommendedReps("8-12").restSeconds(90)
                .isSystemDefault(true).build(),

            Ejercicio.builder()
                .name("Press inclinado con mancuernas")
                .category("Pectorales")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Variante en banco inclinado que enfatiza la porción superior (clavicular) del pectoral.")
                .muscles("Pectoral superior, Deltoides anterior, Tríceps")
                .difficulty("INTERMEDIO")
                .equipment("Mancuernas y banco inclinado")
                .instructions("Ajusta el banco entre 30° y 45°; más inclinación traslada el trabajo al hombro.|"
                        + "Siéntate con una mancuerna sobre cada muslo y súbelas con impulso de las piernas.|"
                        + "Coloca las mancuernas a los lados del pecho, palmas al frente.|"
                        + "Empuja hacia arriba juntándolas ligeramente sin chocarlas.|"
                        + "Baja lento hasta sentir el estiramiento del pectoral, sin pasar la línea del torso.")
                .tips("Un recorrido más amplio que con barra: aprovéchalo bajando controlado.|"
                        + "Mantén las muñecas firmes y alineadas con el antebrazo.|"
                        + "Aprieta el pecho un segundo en la parte alta.")
                .commonMistakes("Inclinar el banco demasiado y convertirlo en press de hombro.|"
                        + "Chocar las mancuernas arriba y perder tensión.|"
                        + "Bajar demasiado y forzar la articulación del hombro.")
                .recommendedSets(3).recommendedReps("10-12").restSeconds(75)
                .isSystemDefault(true).build(),

            Ejercicio.builder()
                .name("Aperturas con mancuernas")
                .category("Pectorales")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Ejercicio de aislamiento que estira y contrae el pectoral en su función de aducción.")
                .muscles("Pectoral mayor, Deltoides anterior")
                .difficulty("PRINCIPIANTE")
                .equipment("Mancuernas y banco plano")
                .instructions("Acuéstate en banco plano con una mancuerna en cada mano sobre el pecho.|"
                        + "Mantén una flexión fija de codos de unos 15°, como si abrazaras un barril.|"
                        + "Abre los brazos en arco hasta la altura del pecho, sintiendo el estiramiento.|"
                        + "Cierra el movimiento juntando las mancuernas arriba, apretando el pectoral.")
                .tips("Usa peso moderado: es un ejercicio de estiramiento, no de fuerza máxima.|"
                        + "El movimiento es un arco, no un press.|"
                        + "No bloquees ni extiendas los codos durante el recorrido.")
                .commonMistakes("Bajar demasiado los brazos y sobrecargar la cápsula del hombro.|"
                        + "Flexionar y extender los codos convirtiéndolo en un press.|"
                        + "Usar cargas excesivas que impiden controlar la bajada.")
                .recommendedSets(3).recommendedReps("12-15").restSeconds(60)
                .isSystemDefault(true).build(),

            // ==================== ESPALDA ====================
            Ejercicio.builder()
                .name("Dominadas")
                .category("Espalda")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Ejercicio de tracción vertical con peso corporal. El mejor indicador de fuerza relativa de espalda.")
                .muscles("Dorsal ancho, Bíceps, Romboides, Trapecio medio")
                .difficulty("AVANZADO")
                .equipment("Barra fija")
                .instructions("Agarra la barra en pronación (palmas al frente), manos algo más anchas que los hombros.|"
                        + "Cuelga con los brazos extendidos y el core activo, piernas ligeramente cruzadas.|"
                        + "Inicia el movimiento bajando los hombros (deprime las escápulas).|"
                        + "Tira con los codos hacia el suelo hasta pasar la barbilla sobre la barra.|"
                        + "Baja controlado hasta la extensión completa sin soltarte de golpe.")
                .tips("Piensa en llevar los codos al bolsillo, no en subir con los brazos.|"
                        + "Si aún no logras una, usa banda elástica o la máquina asistida.|"
                        + "Evita balancearte: cada repetición debe partir desde la posición muerta.")
                .commonMistakes("Hacer medio recorrido sin extender los brazos abajo.|"
                        + "Impulsarse con las piernas (kipping) en entrenamiento de hipertrofia.|"
                        + "Encoger los hombros hacia las orejas durante la tracción.")
                .recommendedSets(4).recommendedReps("6-10").restSeconds(90)
                .isSystemDefault(true).build(),

            Ejercicio.builder()
                .name("Remo con barra")
                .category("Espalda")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Tracción horizontal que desarrolla grosor en la espalda media y fuerza de agarre.")
                .muscles("Dorsal ancho, Romboides, Trapecio, Bíceps, Erectores espinales")
                .difficulty("INTERMEDIO")
                .equipment("Barra")
                .instructions("De pie, pies al ancho de caderas, agarra la barra en pronación.|"
                        + "Flexiona caderas hasta que el torso quede a unos 45° del suelo, espalda neutra.|"
                        + "Deja los brazos colgando y el core apretado.|"
                        + "Lleva la barra hacia el ombligo tirando con los codos pegados al cuerpo.|"
                        + "Aprieta las escápulas arriba y baja controlado sin redondear la espalda.")
                .tips("Mantén la mirada al suelo unos metros adelante para no hiperextender el cuello.|"
                        + "Si sientes la zona lumbar, reduce el peso o apoya el pecho en un banco.|"
                        + "La barra sube y baja pegada a los muslos.")
                .commonMistakes("Redondear la espalda baja: es la principal causa de lesión en este ejercicio.|"
                        + "Usar impulso de piernas y torso en cada repetición.|"
                        + "Tirar solo con los brazos sin retraer las escápulas.")
                .recommendedSets(4).recommendedReps("8-12").restSeconds(90)
                .isSystemDefault(true).build(),

            Ejercicio.builder()
                .name("Jalón al pecho")
                .category("Espalda")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Alternativa en polea a las dominadas; permite regular la carga para trabajar la anchura dorsal.")
                .muscles("Dorsal ancho, Bíceps, Trapecio inferior")
                .difficulty("PRINCIPIANTE")
                .equipment("Polea alta")
                .instructions("Ajusta la almohadilla para que fije bien los muslos.|"
                        + "Agarra la barra más ancho que los hombros, en pronación.|"
                        + "Siéntate con el pecho alto y una ligera inclinación atrás (unos 15°).|"
                        + "Tira de la barra hasta la parte alta del pecho llevando los codos abajo y atrás.|"
                        + "Sube controlado permitiendo que las escápulas se eleven al final.")
                .tips("No lleves la barra detrás de la nuca: es riesgoso para el hombro.|"
                        + "Concéntrate en el dorsal, no en los bíceps.|"
                        + "Mantén el pecho arriba durante todo el recorrido.")
                .commonMistakes("Balancear el torso hacia atrás para vencer el peso.|"
                        + "Recorrido corto que no llega al pecho.|"
                        + "Agarrar la barra con los pulgares muy cerrados y cargar el antebrazo.")
                .recommendedSets(3).recommendedReps("10-12").restSeconds(75)
                .isSystemDefault(true).build(),

            // ==================== PIERNAS ====================
            Ejercicio.builder()
                .name("Sentadilla")
                .category("Piernas")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("El ejercicio rey del tren inferior: desarrolla fuerza global y estabilidad del core.")
                .muscles("Cuádriceps, Glúteos, Isquiotibiales, Core, Erectores espinales")
                .difficulty("INTERMEDIO")
                .equipment("Barra y rack")
                .instructions("Coloca la barra sobre los trapecios (barra alta), no sobre el cuello.|"
                        + "Sácala del rack y da dos pasos atrás; pies al ancho de hombros, puntas ligeramente afuera.|"
                        + "Inhala, llena el abdomen de aire y aprieta el core.|"
                        + "Baja empujando las caderas atrás y flexionando rodillas, manteniendo el pecho alto.|"
                        + "Desciende hasta que los muslos queden paralelos al suelo o más abajo si tu movilidad lo permite.|"
                        + "Sube empujando el suelo con los talones y exhala al pasar el punto difícil.")
                .tips("Las rodillas deben seguir la dirección de las puntas de los pies.|"
                        + "Usa calzado plano o de halterofilia: los amortiguados restan estabilidad.|"
                        + "Si te cuesta la profundidad, trabaja movilidad de tobillo.")
                .commonMistakes("Que las rodillas colapsen hacia dentro al subir.|"
                        + "Levantar los talones del suelo.|"
                        + "Redondear la espalda baja en el fondo del movimiento.|"
                        + "Subir primero la cadera y dejar el torso adelante (se convierte en peso muerto).")
                .recommendedSets(4).recommendedReps("8-12").restSeconds(120)
                .isSystemDefault(true).build(),

            Ejercicio.builder()
                .name("Prensa de piernas")
                .category("Piernas")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Empuje de piernas guiado por máquina; permite cargar el cuádriceps con menor exigencia técnica.")
                .muscles("Cuádriceps, Glúteos, Isquiotibiales")
                .difficulty("PRINCIPIANTE")
                .equipment("Prensa 45°")
                .instructions("Siéntate con la espalda y la cadera bien apoyadas en el respaldo.|"
                        + "Coloca los pies al ancho de caderas en el centro de la plataforma.|"
                        + "Quita los seguros y baja la plataforma flexionando las rodillas.|"
                        + "Desciende hasta unos 90° sin que la cadera se despegue del asiento.|"
                        + "Empuja con toda la planta del pie sin bloquear las rodillas arriba.")
                .tips("Pies más altos enfatizan glúteo e isquios; más bajos, el cuádriceps.|"
                        + "Controla la bajada 2 segundos: ahí está gran parte del estímulo.|"
                        + "Nunca coloques las manos en las rodillas para empujar.")
                .commonMistakes("Bajar tanto que la zona lumbar se despega y se redondea.|"
                        + "Bloquear las rodillas de golpe en la extensión.|"
                        + "Cargar peso excesivo y hacer recorridos muy cortos.")
                .recommendedSets(3).recommendedReps("12-15").restSeconds(90)
                .isSystemDefault(true).build(),

            Ejercicio.builder()
                .name("Peso muerto rumano")
                .category("Piernas")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Bisagra de cadera centrada en isquiotibiales y glúteo; clave para la salud de la cadena posterior.")
                .muscles("Isquiotibiales, Glúteos, Erectores espinales")
                .difficulty("INTERMEDIO")
                .equipment("Barra o mancuernas")
                .instructions("De pie, barra a la altura de las caderas, pies al ancho de caderas.|"
                        + "Mantén una ligera flexión fija de rodillas durante todo el movimiento.|"
                        + "Empuja la cadera hacia atrás bajando la barra pegada a las piernas.|"
                        + "Baja hasta sentir el estiramiento en los isquiotibiales (media tibia aprox.).|"
                        + "Sube apretando los glúteos y llevando la cadera hacia adelante.")
                .tips("El movimiento es de cadera, no de rodilla: no es una sentadilla.|"
                        + "La barra debe rozar las piernas todo el recorrido.|"
                        + "Mantén las escápulas retraídas para no redondear la espalda alta.")
                .commonMistakes("Redondear la espalda al bajar.|"
                        + "Flexionar mucho las rodillas y convertirlo en peso muerto convencional.|"
                        + "Hiperextender la espalda al final del movimiento.")
                .recommendedSets(3).recommendedReps("10-12").restSeconds(90)
                .isSystemDefault(true).build(),

            // ==================== HOMBROS ====================
            Ejercicio.builder()
                .name("Press militar")
                .category("Hombros")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Empuje vertical de pie; construye fuerza de hombro y estabilidad de todo el tronco.")
                .muscles("Deltoides anterior y medio, Tríceps, Core")
                .difficulty("INTERMEDIO")
                .equipment("Barra")
                .instructions("De pie, pies al ancho de caderas, barra apoyada en la parte alta del pecho.|"
                        + "Agarre algo más ancho que los hombros, codos ligeramente adelante.|"
                        + "Aprieta glúteos y abdomen para evitar arquear la espalda.|"
                        + "Empuja la barra recto hacia arriba, retirando la cabeza ligeramente hacia atrás.|"
                        + "Al pasar la frente, lleva la cabeza al frente y bloquea arriba con la barra sobre la coronilla.|"
                        + "Baja controlado hasta la clavícula.")
                .tips("Si la espalda se arquea, reduce el peso: el core es el eslabón débil.|"
                        + "La barra sube en línea recta, no en arco hacia adelante.|"
                        + "Puedes hacerlo sentado con respaldo si buscas aislar más el hombro.")
                .commonMistakes("Arquear la zona lumbar para compensar la carga.|"
                        + "Empujar la barra hacia adelante en vez de vertical.|"
                        + "Usar impulso de piernas sin que sea un push press.")
                .recommendedSets(4).recommendedReps("8-10").restSeconds(90)
                .isSystemDefault(true).build(),

            Ejercicio.builder()
                .name("Elevaciones laterales")
                .category("Hombros")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Aislamiento del deltoides medio; responsable directo de la anchura visual del hombro.")
                .muscles("Deltoides medio, Trapecio superior")
                .difficulty("PRINCIPIANTE")
                .equipment("Mancuernas")
                .instructions("De pie, mancuernas a los costados, ligera flexión de codos.|"
                        + "Inclina el torso apenas hacia adelante y aprieta el core.|"
                        + "Eleva los brazos hacia los lados hasta la altura de los hombros.|"
                        + "Guía el movimiento con los codos, no con las manos.|"
                        + "Baja lento durante 2-3 segundos hasta la posición inicial.")
                .tips("Peso ligero y técnica estricta: es el error más común entrenar pesado aquí.|"
                        + "Imagina que viertes agua de una jarra al llegar arriba.|"
                        + "No subas por encima de la línea del hombro para no involucrar el trapecio.")
                .commonMistakes("Balancear el cuerpo para lanzar las mancuernas.|"
                        + "Encoger los hombros y trabajar el trapecio en vez del deltoides.|"
                        + "Dejar caer el peso sin controlar la fase negativa.")
                .recommendedSets(4).recommendedReps("12-15").restSeconds(45)
                .isSystemDefault(true).build(),

            // ==================== BRAZOS ====================
            Ejercicio.builder()
                .name("Curl con barra")
                .category("Bíceps")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Ejercicio base de bíceps; permite cargar más que las variantes unilaterales.")
                .muscles("Bíceps braquial, Braquial anterior, Antebrazo")
                .difficulty("PRINCIPIANTE")
                .equipment("Barra recta o Z")
                .instructions("De pie, pies al ancho de caderas, barra con agarre supino al ancho de hombros.|"
                        + "Codos pegados al torso y fijos: son el único punto de giro.|"
                        + "Flexiona los codos subiendo la barra hasta la altura del pecho.|"
                        + "Aprieta el bíceps un segundo en la parte alta.|"
                        + "Baja controlado hasta la extensión casi completa.")
                .tips("Si te duelen las muñecas, usa barra Z en vez de recta.|"
                        + "La fase de bajada de 2-3 segundos aumenta el estímulo.|"
                        + "No extiendas totalmente el codo con carga alta para proteger el tendón.")
                .commonMistakes("Balancear la espalda para subir el peso.|"
                        + "Adelantar los codos convirtiéndolo en una remada.|"
                        + "Recorridos cortos que no estiran el bíceps abajo.")
                .recommendedSets(3).recommendedReps("10-12").restSeconds(60)
                .isSystemDefault(true).build(),

            Ejercicio.builder()
                .name("Fondos en paralelas")
                .category("Tríceps")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Empuje vertical con peso corporal; excelente para tríceps y pecho inferior.")
                .muscles("Tríceps, Pectoral inferior, Deltoides anterior")
                .difficulty("INTERMEDIO")
                .equipment("Barras paralelas")
                .instructions("Sujétate de las paralelas y sube hasta tener los brazos extendidos.|"
                        + "Mantén el torso lo más vertical posible para enfatizar el tríceps.|"
                        + "Baja flexionando los codos hasta formar unos 90°.|"
                        + "Mantén los codos cerca del cuerpo, apuntando hacia atrás.|"
                        + "Empuja hacia arriba hasta extender sin bloquear bruscamente.")
                .tips("Si inclinas el torso adelante trabajas más el pectoral.|"
                        + "Empieza con asistencia de banda si no logras el recorrido completo.|"
                        + "No bajes más de 90° si tienes molestias en el hombro.")
                .commonMistakes("Bajar demasiado y comprometer la articulación del hombro.|"
                        + "Balancear las piernas para tomar impulso.|"
                        + "Abrir los codos hacia los lados.")
                .recommendedSets(3).recommendedReps("8-12").restSeconds(75)
                .isSystemDefault(true).build(),

            // ==================== CORE ====================
            Ejercicio.builder()
                .name("Plancha")
                .category("Core")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Ejercicio isométrico que enseña al core a resistir la extensión de la columna.")
                .muscles("Transverso abdominal, Recto abdominal, Oblicuos, Glúteos")
                .difficulty("PRINCIPIANTE")
                .equipment("Peso corporal")
                .instructions("Apóyate sobre antebrazos y puntas de los pies.|"
                        + "Codos justo debajo de los hombros, antebrazos paralelos.|"
                        + "Alinea cabeza, espalda y caderas en una sola línea recta.|"
                        + "Aprieta abdomen y glúteos como si fueras a recibir un golpe.|"
                        + "Respira de forma constante y mantén el tiempo objetivo.")
                .tips("Mejor 30 segundos perfectos que 2 minutos con la cadera caída.|"
                        + "Mira al suelo para mantener el cuello neutro.|"
                        + "Para progresar, añade peso en la espalda o levanta un pie.")
                .commonMistakes("Dejar caer la cadera y arquear la lumbar.|"
                        + "Subir demasiado los glúteos formando una V.|"
                        + "Contener la respiración durante todo el ejercicio.")
                .recommendedSets(3).recommendedReps("30-60 seg").restSeconds(45)
                .isSystemDefault(true).build(),

            Ejercicio.builder()
                .name("Crunch abdominal")
                .category("Core")
                .videoUrl("https://www.youtube.com/watch?v=iA7kjqfMzS4")
                .description("Flexión corta de columna que aísla el recto abdominal en su rango superior.")
                .muscles("Recto abdominal, Oblicuos")
                .difficulty("PRINCIPIANTE")
                .equipment("Peso corporal")
                .instructions("Acuéstate boca arriba con rodillas flexionadas y pies apoyados.|"
                        + "Manos en el pecho o a los lados de la cabeza sin tirar del cuello.|"
                        + "Despega los omóplatos del suelo enrollando la columna hacia arriba.|"
                        + "Aprieta el abdomen un segundo en la parte alta.|"
                        + "Baja controlado sin dejar caer la espalda de golpe.")
                .tips("El recorrido es corto: no necesitas sentarte por completo.|"
                        + "Exhala al subir para contraer más el abdomen.|"
                        + "Deja un espacio del tamaño de un puño entre el mentón y el pecho.")
                .commonMistakes("Tirar de la cabeza con las manos y cargar el cuello.|"
                        + "Usar impulso y rebotar en el suelo.|"
                        + "Hacerlo tan rápido que se pierde la contracción.")
                .recommendedSets(3).recommendedReps("15-20").restSeconds(45)
                .isSystemDefault(true).build()
        ));

        completarVideoUrlsFaltantes();
        asignarFotosEjercicios();
    }

    /**
     * Completa el videoUrl de ejercicios ya sembrados en una ejecución anterior (antes de
     * agregar este campo al seed) y que por lo tanto quedaron con videoUrl en null. Solo
     * rellena lo que esté vacío: si el ejercicio ya tiene su propio video (asignado a mano
     * desde el panel de administración), no se pisa. Se ejecuta en cada arranque, igual que
     * los métodos "reparar*" de TrainNow-Usuarios.
     */
    private void completarVideoUrlsFaltantes() {
        var sinVideo = repository.findAll().stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsSystemDefault()))
                .filter(e -> e.getVideoUrl() == null || e.getVideoUrl().isBlank())
                .toList();
        if (sinVideo.isEmpty()) return;
        sinVideo.forEach(e -> e.setVideoUrl(VIDEO_URL_DEMO));
        repository.saveAll(sinVideo);
    }

    /**
     * @Column(columnDefinition = "TEXT") no basta para una foto real comprimida (800px, JPEG)
     * en base64: TEXT de MySQL solo llega a 65 535 bytes. Con ddl-auto=update, Hibernate no
     * altera el tipo de una columna que ya existía como TEXT, así que se fuerza aquí el ALTER
     * TABLE de forma idempotente (mismo criterio que asegurarColumnaFotoAmplia en
     * TrainNow-Usuarios).
     */
    private void asegurarColumnaImagenAmplia() {
        try {
            jdbcTemplate.execute("ALTER TABLE exercises MODIFY COLUMN image_url MEDIUMTEXT");
        } catch (Exception e) {
            log.warn("No se pudo ampliar la columna image_url a MEDIUMTEXT: {}", e.getMessage());
        }
    }

    /**
     * Asigna las fotos reales que Santiago va mandando ejercicio por ejercicio (ver
     * FOTOS_EJERCICIOS), cargadas desde resources/ejercicios/. Solo completa lo que esté
     * vacío: si el ejercicio ya tiene imagen propia (subida desde el panel de administración),
     * no se pisa. Se ejecuta en cada arranque, así que basta con agregar la entrada al mapa y
     * el archivo en resources/ para que la próxima vez que corra el servicio quede asignada.
     */
    private void asignarFotosEjercicios() {
        var porNombre = repository.findAll().stream()
                .collect(Collectors.toMap(e -> e.getName().toLowerCase(), e -> e, (a, b) -> a));
        FOTOS_EJERCICIOS.forEach((nombre, archivo) -> {
            Ejercicio ejercicio = porNombre.get(nombre.toLowerCase());
            if (ejercicio == null) return;
            if (ejercicio.getImageUrl() != null && !ejercicio.getImageUrl().isBlank()) return;
            String dataUri = cargarImagenComoDataUri("ejercicios/" + archivo);
            if (dataUri != null) {
                ejercicio.setImageUrl(dataUri);
                repository.save(ejercicio);
                log.info("Foto asignada a {}", nombre);
            }
        });
    }

    /** Lee un archivo JPEG del classpath (resources/) y lo devuelve como data URI base64. */
    private String cargarImagenComoDataUri(String rutaClasspath) {
        try (InputStream in = new ClassPathResource(rutaClasspath).getInputStream()) {
            byte[] bytes = in.readAllBytes();
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            log.warn("No se pudo cargar la imagen {}: {}", rutaClasspath, e.getMessage());
            return null;
        }
    }
}

import kotlin.random.Random

class Modulo(maxAlumnos:Int) {
    var alumnos:Array<Alumno?> = kotlin.arrayOfNulls(maxAlumnos)
    var evaluaciones:Array<Array<Float>> = Array(4){ Array(maxAlumnos){-1.0F}}
    companion object{
        const val LIMITE_ALUMNOS = 15
        const val PRIMERA_EVALUACION = 0
        const val SEGUNDA_EVALUACION = 1
        const val TERCERA_EVALUACION = 2
        const val EVALUACION_FINAL = 3
    }

    //Función auxiliar
    fun numeroEvaluacion(evaluacion: String): Int{
        return when(evaluacion.uppercase()){
            "PRIMERA" -> PRIMERA_EVALUACION
            "SEGUNDA" -> SEGUNDA_EVALUACION
            "TERCERA" -> TERCERA_EVALUACION
            else -> EVALUACION_FINAL
        }
    }

    //Establecer la nota de un alumno en una evaluación dada
    fun establecerNota(idAlumno:String, evaluacion:String, nota:Float): Boolean{
        if (posicionPorID(idAlumno) == -1)
            return false

        evaluaciones[numeroEvaluacion(evaluacion)][posicionPorID(idAlumno)] = nota
        return true
    }

    //12. Matricular alumno
    fun matricularAlumno(alumno: Alumno?): Boolean {
        var indice = 0
        for(estudiante in alumnos) {
            if (estudiante?.id == alumno?.id) {
                println("Ya existe un alumno con la ID " + estudiante?.id)
                return false
            }
        }

        for(estudiante in alumnos) {
            if (estudiante == null) {
                alumnos[indice] = alumno
                println("Alumno matriculado con éxito.")
                return true
            }
            indice++
        }
        return false
    }

    //2. Calcular nota final
    fun calculaEvaluacionFinal() {
        var notaMediaSinRedondear: Float
        for (i in 0..(alumnos.size - 1)) {
            notaMediaSinRedondear = (evaluaciones[PRIMERA_EVALUACION][i] + evaluaciones[SEGUNDA_EVALUACION][i] + evaluaciones[TERCERA_EVALUACION][i]) / 3.0F
            evaluaciones[EVALUACION_FINAL][i] = (Math.round(notaMediaSinRedondear * 100.00F) / 100.00F) ////Se multiplica por 100 antes de redondear para luego, al dividir por 100, tener 2 decimales.
        }
    }

    //3. Lista alumnos, notas por evaluación. Por defecto la final.
    fun listaNotas(evaluacion: String = "Final"):List<Pair<String?, Float>> {
        var lista:List<Pair<String?, Float>> = listOf()
        var id: String?
        var nota: Float

        alumnos.filterIndexed { index, it -> index >= 0 && it != null }.forEach{
            id = it?.id
            nota = evaluaciones[numeroEvaluacion(evaluacion)][alumnos.indexOf(it)]

            lista += Pair(id, nota)
        }

        return lista
    }

    //4. Cuántos han aprobado por evaluación
    fun numeroAprobados(evaluacion: String = "Final") = evaluaciones[numeroEvaluacion(evaluacion)].count { it >= 5 }

    //5. Nota más baja
    fun notaMasBaja(evaluacion: String = "Final") = evaluaciones[numeroEvaluacion(evaluacion)].filter { it > -1}.minOrNull()

    //6. Nota más alta
    fun notaMasAlta(evaluacion: String = "Final") = evaluaciones[numeroEvaluacion(evaluacion)].maxOrNull()

    //7. Nota media
    fun notaMedia(evaluacion: String = "Final") = (Math.round(evaluaciones[numeroEvaluacion(evaluacion)].filter { it > -1}.average() * 100.00F) / 100.00F)  //Se multiplica por 100 antes de redondear para luego, al dividir por 100, tener 2 decimales.

    //8. ¿Hay algún 10?
    fun hayAlumnosConDiez(evaluacion: String = "Final") = evaluaciones[numeroEvaluacion(evaluacion)].count { it == 10.0F } > 0

    //9. ¿Hay aprobados?
    fun hayAlumnosAprobados(evaluacion: String = "Final") = numeroAprobados(evaluacion) > 0

    //10. Primera nota que no ha superado el 5
    fun primeraNotaNoAprobada(evaluacion: String = "Final") = evaluaciones[numeroEvaluacion(evaluacion)].filter { it < 5 }.maxOrNull()


    //11. Lista de alumnos ordenados según su nota
    fun listaNotasOrdenados(evaluacion: String = "Final") = listaNotas(evaluacion).sortedBy{it.second}

    // Auxiliar
    fun posicionPorID(id: String): Int {
        var indice = 0
        for (alumno in alumnos) {
            alumno?.let {
                if (alumno.id == id)
                    return indice
            }
            indice++
        }
        return -1
    }

    //13. Dar de baja alumnos
    fun bajaAlumno(id: String):Boolean {
        val indice = posicionPorID(id)
        if (indice != -1){
            alumnos[indice] = null  //Eliminamos el objeto alumno
            for(ev in PRIMERA_EVALUACION..EVALUACION_FINAL) //Establecemos a -1 sus notas
                evaluaciones[ev][indice] = -1.0F

            println("Esta es la nueva lista de alumnos: ")
            println(listaNotas())
            return true
        }
        else {
            println("No hay ningún alumno con ese ID.")
            return false
        }
    }

    //Pedir id de alumno (para dar de baja)
    fun pedirIDAlumno():String {
        print("Introduzca la ID: ")
        return readLine()?:"ERROR"
    }

    //OPCIONAL: MENÚ PARA MOSTRAR AL USUARIO
    fun mostrarMenu():Int{
        println("--- Menú del módulo ---")

        println("1. Salir")
        println("2. Calcular las notas de la evaluación final (mediante media aritmética).")
        println("3. Listado de notas de los alumnos de una evaluación.")
        println("4. Indicar el número de aprobados de una evaluación.")
        println("5. Indicar la nota más baja de una evaluación.")
        println("6. Indicar la nota más alta de una evaluación.")
        println("7. Indicar la nota media de una evaluación.")
        println("8. Indicar si hay, al menos, un alumno con un 10 en una evaluación.")
        println("9. Indicar si hay, al menos, un alumno aprobado en una evaluación.")
        println("10. Indicar, de los suspensos, la nota más cercana al aprobado.")
        println("11. Listado de notas de los alumnos de una evaluación, ordenados de forma ascendente.")
        println("12. Matricular un alumno nuevo.")
        println("13. Dar de baja a un alumno.")

        print("\nIntroduzca su opción: ")
        return (readLine()?:"1").toInt()
    }

    fun menu(opcion:Int){
        when(opcion){
            2 -> calculaEvaluacionFinal()
            3 -> println(listaNotas(solicitarEvaluacion()))
            4 -> println(numeroAprobados(solicitarEvaluacion()))
            5 -> println(notaMasBaja(solicitarEvaluacion()))
            6 -> println(notaMasAlta(solicitarEvaluacion()))
            7 -> println(notaMedia(solicitarEvaluacion()))
            8 -> println(hayAlumnosConDiez(solicitarEvaluacion()))
            9 -> println(hayAlumnosAprobados(solicitarEvaluacion()))
            10 -> println(primeraNotaNoAprobada(solicitarEvaluacion()))
            11 -> println(listaNotasOrdenados(solicitarEvaluacion()))
            12 -> matricularAlumno(solicitarAlumno())
            13 -> bajaAlumno(pedirIDAlumno())
        }
    }

    fun solicitarEvaluacion():String{
        print("Introduzca la evaluación deseada: ")
        var eval = (readLine()?:"Final").uppercase()
        if (eval != "PRIMERA" && eval != "SEGUNDA" && eval != "TERCERA") {
            println("Entrada no reconocida, se considerará la evaluación FINAL.")
            eval = "FINAL"
        }
        return eval
    }

    fun solicitarAlumno():Alumno{
        print("Introduzca la ID: ")
        val id = (readLine()?:"1")
        print("Introduzca el nombre: ")
        val nombre = (readLine()?:"")
        print("Introduzca el primer apellido: ")
        val apellido1 = (readLine()?:"")
        print("Introduzca el segundo apellido: ")
        val apellido2 = (readLine()?:"")

        return Alumno(id, nombre, apellido1, apellido2)
    }

}

class Alumno(val id: String, val nombre: String){
    var ap1: String = ""
    var ap2: String = ""

    constructor(id:String, nombre:String, ap1:String):this(id, nombre){
        this.ap1 = ap1
    }

    constructor(id:String, nombre:String, ap1:String, ap2:String):this(id, nombre, ap1){
        this.ap2 = ap2
    }
}

fun inicializarEjemplo(): Modulo {
    val modulo = Modulo(Modulo.LIMITE_ALUMNOS)
    //Añadimos a 10 alumnos
    modulo.matricularAlumno(Alumno("1", "Alejandro", "Ogalla", "López"))
    modulo.matricularAlumno(Alumno("2", "Sandra", "Pérez", "Montilla"))
    modulo.matricularAlumno(Alumno("3", "Tony", "Soprano"))
    modulo.matricularAlumno(Alumno("4", "María", "Alonso", "López"))
    modulo.matricularAlumno(Alumno("5", "Sofía", "Pardo", "Velázquez"))
    modulo.matricularAlumno(Alumno("6", "Pedro Luis", "Gómez", "Moya"))
    modulo.matricularAlumno(Alumno("7", "Lucas", "Pérez", "Kolll"))
    modulo.matricularAlumno(Alumno("8", "Antonio Jesús", "González"))
    modulo.matricularAlumno(Alumno("9", "Wout", "van Aert"))
    modulo.matricularAlumno(Alumno("10", "Francisco José", "Castro", "Quijano"))

    //Añadimos las notas de las 3 evaluaciones a cada uno de los 10 alumnos
    for(i in 0..2)
        for(k in 0..9)
            modulo.evaluaciones[i][k] = (Math.round(Random.nextFloat() * 10.00F * 100.00F) / 100.00F) //Se multiplica por 100 antes de redondear para luego, al dividir por 100, tener 2 decimales.

    return modulo
}



fun main() {
    val modulo = inicializarEjemplo()
    modulo.calculaEvaluacionFinal();

    var opcion:Int
    do {
        opcion = modulo.mostrarMenu()
        modulo.menu(opcion)
    }while (opcion != 1)
}
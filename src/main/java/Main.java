import controller.GestorInvocadores;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GestorInvocadores gestor = new GestorInvocadores();
        int opcion = 0;
        do {
            System.out.println("--- BUSCADOR PERFILES LEAGUE OF LEGENDS ---");
            System.out.println("1. Buscar invocador y añadir a la lista");
            System.out.println("2. Ver lista guardada");
            System.out.println("3. Importar lista");
            System.out.println("4. Exportar lista");
            System.out.println("5. Salir");
            System.out.print("Elige una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("Introduce el nombre de invocador (ej: Ricardo 04): ");
                    String gameName = scanner.nextLine();

                    System.out.print("Introduce el tagline (ej: EUW): ");
                    String tag = scanner.nextLine();

                    gestor.buscarYAñadirInvocador(gameName, tag);
                    break;

                case 2:
                    gestor.mostrarListaGuardada();
                    break;

                case 3:
                    System.out.println("Importando datos");
                    gestor.importarLista();
                    break;

                case 4:
                    System.out.println("Exportando datos");
                    gestor.exportarYSalir();
                    break;

                case 5:
                    System.out.println("SALIENDO...");
                    break;

                default:
                    System.out.println("Opción no contemplada. Elige un número del 1 al 5.");
            }

        } while (opcion != 5);

        scanner.close();
    }
}
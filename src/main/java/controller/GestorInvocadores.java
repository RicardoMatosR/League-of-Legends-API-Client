package controller;

import model.Invocador;

import java.util.ArrayList;

public class GestorInvocadores {
    private ArrayList<Invocador> invocadores; //lista a exportar e importar
    private APIController apiController;
    private FileController fileController;

    public GestorInvocadores() {
        invocadores = new ArrayList<>();
        apiController = new APIController();
        fileController = new FileController();
    }

    public void buscarYAñadirInvocador(String nombre, String tag) {
        Invocador invocadorEncontrado = apiController.obtenerPuuidYDevolverInvocador(nombre, tag);
        if (invocadorEncontrado != null) {
            invocadores.add(invocadorEncontrado);
            System.out.println(nombre + "#" + tag + " añadido a la lista.");
            apiController.conseguirRango(invocadorEncontrado.getPuuid());
            apiController.imprimir3Mains(invocadorEncontrado.getPuuid());
            System.out.println();
            apiController.verUltimaPartida(invocadorEncontrado.getPuuid());
        } else {
            System.out.println("No se pudo encontrar a " + nombre + "#" + tag);
        }
    }


    public void mostrarListaGuardada() {
        if (invocadores.isEmpty()) {
            System.out.println("Ningun invocador añadido a la lista");
        } else {
            System.out.println("INVOCADORES:");
            for (Invocador invocador : invocadores) {
                System.out.println(invocador.getGameName() + "#" + invocador.getTagLine());
            }
        }
    }

    public void importarLista() {
        invocadores = fileController.importarLista();
        System.out.println("Datos importados");
    }

    public void exportarYSalir() {
        fileController.exportarLista(invocadores);
        System.out.println(" Datos guardados");
        apiController.closeClient();
    }
}

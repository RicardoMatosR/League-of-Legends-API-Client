package controller;

import model.Invocador;

import java.io.*;
import java.util.ArrayList;

public class FileController {
    private static final String RUTA = "src/main/java/resources/listaImportarExportar.obj";

    // try-with-resources cierra el fichero siempre, aunque haya error
    public void exportarLista(ArrayList<Invocador> invocadores) {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(RUTA))) {
            salida.writeObject(invocadores);
        } catch (IOException e) {
            System.out.println("No se pudo guardar la lista: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Invocador> importarLista() {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(RUTA))) {
            return (ArrayList<Invocador>) entrada.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No se pudo leer la lista: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}

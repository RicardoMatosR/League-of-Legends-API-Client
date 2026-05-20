package controller;

import model.Invocador;

import java.io.*;
import java.util.ArrayList;

public class FileController {
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

    public void exportarLista(ArrayList<Invocador> invocadores){
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream("src/main/java/resources/listaImportarExportar.obj"));
            objectOutputStream.writeObject(invocadores);
        } catch (IOException e) {
            System.out.println("Ruta de creacion incorrecta");
        } finally {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                System.out.println("Error en el cerrado");
            }
        }
    }

    public ArrayList<Invocador> importarLista(){
        ArrayList<Invocador> invocadores = new ArrayList<>();
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream("src/main/java/resources/listaImportarExportar.obj"));
            invocadores = (ArrayList<Invocador>) objectInputStream.readObject();
        } catch (IOException e) {
            System.out.println("Error en ruta");
        } catch (ClassNotFoundException e) {
            System.out.println("Error al no encontrar la clase");
        } finally {
            try {
                if (objectInputStream != null){
                    objectInputStream.close();
                }
            } catch (IOException e) {
                System.out.println("Error cerrando archivo al importar");
            }
        }
        return invocadores;
    }
}
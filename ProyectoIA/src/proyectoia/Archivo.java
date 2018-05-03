/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoia;

import java.awt.FileDialog;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Willy
 */
public class Archivo {
    FileDialog fd = null;
    RandomAccessFile lector = null;
    Principal form = new Principal();
    String nombreArchivo;
    long tamArchivo;
    byte[] buffer = null;
    String error="";
    long posicionActual=0;
    String charActual="";
    List<String> Palabras = new ArrayList<String>();
    String Etiqueta = "";
    List<Bolsa> Bobeda = new ArrayList<Bolsa>();
    
    public Archivo()
    {
        //this.nodoKey = new ArrayList();
        fd = new FileDialog(form, "Abrir archivo", FileDialog.LOAD);
    }
    public void Cargar() throws IOException
    {
        fd.setVisible(true);
        nombreArchivo = fd.getDirectory() + fd.getFile();
        if(!nombreArchivo.equals("nullnull"))
        {
            //Validar Archivo de entrada
            ValidarArchivo();
            if (error.equals("")) {
                error = "Archivo sin errores.";
            }
        }
        else
        {
            error = "No file selected";
        }
    }
    public void Leer(String nombreA, int size, long pos) throws FileNotFoundException, IOException
    {
        lector = new RandomAccessFile(nombreA, "r");
        buffer = new byte[size];
        tamArchivo = lector.length();
        lector.seek(pos);
        lector.read(buffer);
        lector.close();
    }   
    
    public void ValidarArchivo() throws IOException{
        int Estado = 0;
        boolean bFinal = false;
        while(bFinal==false){
            Leer(nombreArchivo, 1, posicionActual);
            charActual = new String(buffer).toLowerCase();
            switch(Estado){
                case 0:{
                    switch(charActual){
                        case" ":{
                            Estado=0;
                            break;
                        }
                        default:{
                            if (Palabra()) {
                                Estado=1;
                            }
                            else{
                                error="Caracter no permitido en el archivo.";
                                bFinal=true;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 1:{
                    switch(charActual){
                        case",":{
                            Estado=0;
                            break;
                        }
                        case" ":{
                            Estado=2;
                            break;
                        }
                        case"|":{
                            Estado=3;
                            break;
                        }
                        default:{
                            error="Caracter no permitido en el archivo.";
                            bFinal=true;
                            break;
                        }
                    }
                    break;
                }
                case 2:{
                    switch(charActual){
                        case" ":{
                            Estado=0;
                            break;
                        }
                        case"|":{
                            Estado=3;
                            break;
                        }
                        default:{
                            if (Palabra()) {
                                Estado=0;
                            }
                            else{
                                error="Caracter no permitido en el archivo.";
                                bFinal=true;
                            }
                        }
                    }
                    break;
                }
                case 3:{
                    switch(charActual){
                        case" ":{
                            Estado=3;
                            break;
                        }
                        default:{
                            if (Etiqueta()) {
                                Estado=4;
                            }
                            else{
                                error="Caracter no permitido en el archivo.";
                                bFinal=true;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 4:{
                    switch(charActual){
                        case" ":{
                            Estado=4;
                            break;
                        }
                        case"\r":{
                            Estado=0;
                            //Agregar a la bolsa
                            AgregarPalabras();
                            Etiqueta="";
                            Palabras.clear();
                            posicionActual++;
                            break;
                        }
                        case"\0":{
                            Estado=5;
                            AgregarPalabras();
                            Etiqueta="";
                            Palabras.clear();
                            bFinal=true;
                            break;
                        }
                        default:{
                            error="Caracter no permitido en el archivo.";
                            bFinal=true;
                            break;
                        }
                    }
                    break;
                }
                default:{
                    error="Caracter no permitido en el archivo.";
                    bFinal=true;
                    break;
                }
            }
            posicionActual++;
        }
    }
    
    public boolean Palabra() throws IOException{
        boolean bWord=false;
        String palabra="";
        if (Character.isLetter(charActual.charAt(0))){
            while (Character.isLetter(charActual.charAt(0))) {
                palabra+=charActual;
                posicionActual++;
                Leer(nombreArchivo, 1, posicionActual);
                charActual = new String(buffer).toLowerCase();          
            }
            posicionActual--;
            bWord=true;
        }
        if (palabra.length()>3) {
            Palabras.add(palabra);
        }
        return bWord;
    }
    
    public boolean Etiqueta() throws IOException{
        boolean bWord=false;
        if (Character.isLetter(charActual.charAt(0))){
            while (Character.isLetter(charActual.charAt(0))) {
                Etiqueta+=charActual;
                posicionActual++;
                Leer(nombreArchivo, 1, posicionActual);
                charActual = new String(buffer).toLowerCase();       
            }
            posicionActual--;
            bWord=true;
        }
        return bWord;
    }
    
    public void AgregarPalabras(){
        Bolsa Elemento = new Bolsa();
        String P="";
        boolean agregado = false;
        if (Bobeda.isEmpty()) {
            Elemento.Etiqueta = Etiqueta;
            for (int i = 0; i < Palabras.size(); i++) {
                P=Palabras.get(i);
                Elemento.Palabras.add(P);
            }
            Bobeda.add(Elemento);
        }
        else{
            for (int i = 0; i < Bobeda.size(); i++) {
                if (Bobeda.get(i).Etiqueta.equals(Etiqueta)){//Si la etiqueta ya existe, no se agrega.
                    for (int j = 0; j < Palabras.size(); j++) {
                        if (!Bobeda.get(i).Palabras.contains(Palabras.get(j))) {
                            P=Palabras.get(j);
                            Elemento.Palabras.add(P);
                        }
                    }
                    agregado = true;
                }
            }
            if (!agregado) {
                Elemento.Etiqueta = Etiqueta;
                for (int i = 0; i < Palabras.size(); i++) {
                    P=Palabras.get(i);
                    Elemento.Palabras.add(P);
                }
            }
            Bobeda.add(Elemento);
        }
    }
}

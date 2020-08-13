package imagenes;

import javax.swing.*;
import java.awt.Image.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.String;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Imagenes extends JFrame
{

private PanelImagen PI = new PanelImagen();

// Constructor de la clase
public Imagenes (boolean opci)
{
setTitle("Cifrado/Descifrado de Imágenes");
setLocation(300, 25);
setSize(800, 700);
Container Cpane = this.getContentPane();
Cpane.setLayout(new BorderLayout());
this.getContentPane().add(PI);
Principal prin = new Principal();
prin.setVisible(true);
PI.opcion(opci);
}
}

class PanelImagen extends JPanel
{
Image homer = null;
ImageIcon ic = null;
BufferedImage bi, enc, denc;
int ancho; //ancho de la imagen
int alto;  // alto de la imagen
int x, y;
int matriz [][]; // matriz de pixeles encriptados
int matriz2 [][]; // matriz de pixeles desencriptados
int rgbs [];     // matriz de pixeles de la imagen
int ingresar []; // matriz de pixeles de la imagen encriptados
int desenc []; // matriz de pixeles desencriptados debe ser igual a rgbs
int  f, p, clave, largo; // f numero fragmentos, p numero que desordena
int l; // total de pixeles a fragmentar
int cont = 0;
boolean opcion = false;

public void opcion(boolean op){
    this.opcion = op;
}

public PanelImagen()
{
//Ingreso de datos
//Abrir Imagen
    JFileChooser fileChooser = new JFileChooser();
    FileNameExtensionFilter filter=new FileNameExtensionFilter("Archivo de Imagen","jpg");
    fileChooser.setFileFilter(filter);
    int option = fileChooser.showOpenDialog(this);
    if(option == JFileChooser.APPROVE_OPTION){
        String ruta = fileChooser.getSelectedFile().getPath();
        ImageIcon icono=new ImageIcon(ruta);
        ic = icono;
        homer = icono.getImage();
    }
    

String pasword = JOptionPane.showInputDialog("Ingrese clave de 4 dígitos: ");

// Nº de fragmentos 
f = 7000; 
// Converir String a valor entero
if (pasword == null || pasword.equalsIgnoreCase(""))
clave = 1234; // Clave por defecto
else
clave = Integer.parseInt(pasword);

//Adquirir el ancho y alto de la imagen
if (homer != null)
{
ancho = ic.getIconWidth();
alto = ic.getIconHeight();
System.out.println( "ancho y alto son" + ancho + alto);
}

p = clave % f;
while (p == 0)
{
clave = clave + 3;
p = clave % f;
}
if ((p % 2) == 0 && p > 1 && (f % 2) == 0)
p = p - 1;
if ((p % 2) != 0 && (f % 2) != 0 && p > 1)
p = p - 1;

l = f * ((ancho * alto) / f);
largo = l / f;  // largo en pixeles de cada fragmento
}


// Metodo que guarda la imagen dentro de un buffer
public static BufferedImage toBufferedImage(Image image)
{
if (image instanceof BufferedImage)
{
return (BufferedImage)image;
}
// Aqui se asegura quee todos los pixeles de la imagen sean cargados
image = new ImageIcon(image).getImage();
// Se crea un BufferedImage con un formato que sea compatible con la
// ventana a visualizar
BufferedImage bimage = null;
GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
try
{
// Se le entrega un tipo de transparencia al BufferedImage
int transparency = Transparency.OPAQUE;
//  Se llena el BufferedImage
GraphicsDevice gs = ge.getDefaultScreenDevice();
GraphicsConfiguration gc = gs.getDefaultConfiguration();
bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null)
, transparency);
}
catch (Exception e)
{
// El sistema no permite visualizar
}
if (bimage == null)
{
// Se crea el BufferedImage usando un color Model por defecto
int type = BufferedImage.TYPE_INT_RGB;
bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
}
// Se prepara al buffer para copiar la imagen
Graphics g = bimage.createGraphics();
// Se pinta la imagen dentro del buffer
g.drawImage(image, 0, 0, null);
g.dispose();
return bimage;
}

public void paintComponent (Graphics g)
{
int red, green, blue;
super.paintComponent(g);       // paint background
Graphics2D g2 = (Graphics2D)g; // Se necesito a Graphics 2D

if (homer != null)
{
bi = toBufferedImage(homer); // Se copia la imagen al buffer
}

g2.drawImage(bi, 10, 10, this); // Dibuja la imagen dentro del panel
enc = bi; // Se copia la imagen en otro buffer para luego encriptar

// Para encriptar
if (opcion == true)
{
// Arreglo que guardara los valores de pixeles de la imagen
rgbs = new int[ancho * alto];
// Se copia en el arreglo el valor de cada pixel
bi.getRGB(0, 0, ancho, alto, rgbs, 0, ancho);
// Se llama a encriptar la imagen en el arreglo matriz
encriptaImagen();
// Se copia el nuevo valor de cada pixel en el buffer
if (matriz != null)
{
cont = 0;
for (int i = 0; i < alto; i++)
{
for (int j = 0; j < ancho; j++)
{
enc.setRGB(j, i, ingresar[cont]);
cont = cont + 1;
}
}
}
// Se dibuja en el panel la imagen encriptada
g2.drawImage(enc, 250, 320, this);
try
{
// Se guarda la imagen encriptada en una archivo .jpg
File outputFile = new File("cifrada.jpg");
ImageIO.write(enc, "JPG", outputFile);
}
catch (IOException e)
{
System.out.println(e);
}
}
else
{
// Para desencriptar
rgbs = new int[ancho * alto];
//denc = enc; // Se copia la imagen en otro buffer para luego desencriptar
denc = enc;
// Copiamos en rgbs los pixeles ahora encriptados

enc.getRGB(0, 0, ancho, alto, rgbs, 0, ancho);

// LLamamos a desencriptaImagen

desencriptaImagen();

// Se copia el valor desencriptado de cada pixel en el buffer denc

if (matriz2 != null)
{
cont = 0;
for (int i = 0; i < alto; i++)
{
for (int j = 0; j < ancho; j++)
{
denc.setRGB(j, i, desenc[cont]);
cont = cont + 1;
}
}
}
// Se dibuja en el panel la imagen desencriptada
g2.drawImage(denc, 250, 320, this);

try
{
// Se guarda la imagen encriptada en una archivo .jpg
File outputFile = new File("descifrada.jpg");
ImageIO.write(denc, "JPG", outputFile);
}

catch (IOException e)
{
System.out.println(e);
}
}
}

// Metodo que encripta el la imagen que se copio al arreglo rgbs[]
public void encriptaImagen ()
{
int cont = 0;
int a[][] = new int [largo][f];

// Guarda la imagen por fragmentos de filas en otro arreglo de columnas
for (int i = 0; i < f; i++)
{
for (int j = 0; j < largo; j++)
{
a [j][i] = rgbs[cont];
cont = cont + 1;
}
}
// Matriz tendra los valores desordenados de los pixeles de la imagen original
matriz = new int [largo][f];

// Se procede a encriptar los fragmentos de la imagen
for (int i = 1; i <= f; i++)
{
for (int j = 0; j < largo; j++)
{
matriz[j][(i * p) % f] = a[j][i - 1];
}
}

// Se copia la imagen encriptada en un arreglo unidimensional para
// despues almacenarlos en el buffer

cont = 0;
ingresar = new int [ancho * alto];
for (int i = 0; i < f; i++)
{
for (int j = 0; j < largo; j++)
{
ingresar [cont] = matriz[j][i];
cont = cont + 1;
}
}
}

public void desencriptaImagen ()
{
// Tenemos en el buffer enc la imagen encriptada
// Se procede a desencriptar la imagen
int b [][] = new int [largo][f];
int cont = 0;
for (int i = 0; i < f; i++)
{
for (int j = 0; j < largo; j++)
{
b [j][i] = rgbs[cont];
cont = cont + 1;
}
}

// matriz que tendra los valores ordenados nuevamente
matriz2 = new int [largo][f];
for (int i = 1; i <= f; i++)
{
for (int j = 0; j < largo; j++)
{
matriz2[j][i - 1] = b[j][(i * p) % f];
}
}

// Se copia la imagen desencriptada en un arreglo unidimensional para
// despues almacenarlos en el buffer final
cont = 0;
desenc = new int [ancho * alto];

for (int i = 0; i < f; i++)
{
for (int j = 0; j < largo; j++)
{
desenc [cont] = matriz2[j][i];
cont = cont + 1;
}
}
} // fin del metodo
} // fin de la clase
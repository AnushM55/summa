package com.ptds;

import java.awt.Desktop;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.nio.file.Path;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
public class TicketPage implements Initializable{
  
  @FXML TextField PlatformNumber;
  @FXML TextField TicketID;
  @FXML TextField NoOfPersons;
  @FXML TextField Amount;
  @FXML TextField Time;
  @FXML TextField Date;
  @FXML Button SubmitButtonForTag;
  @FXML Button SubmitButton;
  boolean Populated;

 private static String TransactionID;
 static void SetTransactionID(String ID){
        TransactionID = ID;
 }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
      // TODO Auto-generated method stub
      TicketID.setText(new RandomNumber(10).GenerateNumber());
      NoOfPersons.setText(PassengerInformation.getPassengerInformation().size()+"");
      Amount.setText((PassengerInformation.getPassengerInformation().size()*GuestDetails.amount)+"");
      Date.setText(java.time.LocalDate.ofInstant(java.time.Instant.now(),java.time.ZoneId.systemDefault()).toString());
      Time.setText(java.time.LocalTime.now().toString().replaceFirst("..........$",""));
      SubmitButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
                try {
                    PopulateGuestInfo();
                    PopulatePlatformInfo();
                    Populated = true;
                    PrintTicket();
                    
                } catch (Exception e) {
                        System.err.println(e.getLocalizedMessage());
                       for(StackTraceElement x : e.getStackTrace()){
                        System.err.println(x.toString());
                       }
                }
          }
        
      });
      SubmitButtonForTag.setOnAction(new javafx.event.EventHandler<ActionEvent>(){
          @Override
          public void handle(ActionEvent event) {
            if(!Populated){
                SubmitButton.fire();
            }
            try{
            printToTag();
          }catch(IOException e ){
                System.err.println("Exception while printing tag");
          }catch(WriterException e){
            System.err.println("writer exception while printing tag");
          }
        }
        
    });

     
  }
   public  void printToTag() throws IOException, WriterException {
        org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName font_name_3v = org.apache.pdfbox.pdmodel.font.Standard14Fonts.getMappedFontName("COURIER");
        PDFont pdfFont = new PDType1Font(font_name_3v.COURIER);
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Generate QR code
        String qrContent = "Ticket ID: " + TicketID.getText() + "\n"
                         + "Date: " + Date.getText() + "\n"
                         + "Time: " + Time.getText() + "\n"
                         + "Persons: " + NoOfPersons.getText() + "\n"
                         + "Platform No: " + PlatformNumber.getText() + "\n"
                         + "Amount: " + Amount.getText();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);       // Save QR code as an image
        File qrFile = new File("qr_code.png");
        ImageIO.write(qrImage, "PNG", qrFile);

        // Add QR code image to the PDF
        PDImageXObject qrImageXObject = PDImageXObject.createFromFile("qr_code.png", document);
        float qrX = (page.getMediaBox().getWidth() - 200) / 2;
        float qrY = (page.getMediaBox().getHeight() - 200);

        // Set font and begin text below the QR code
        contentStream.setFont(new PDType1Font(font_name_3v.HELVETICA_BOLD), 20);
        contentStream.beginText();
        contentStream.setLeading(14.5f);
        contentStream.newLineAtOffset(qrX, qrY - 40);
        contentStream.showText("SASA RAILWAYS PLATFORM TICKET");
        contentStream.newLine();
        contentStream.newLine();
        contentStream.setFont(new PDType1Font(font_name_3v.COURIER), 12);
        contentStream.showText("Ticket details are in the QR code above.");
        contentStream.newLine();
        contentStream.showText("CONTACT NEARBY POLICE STATION IN CASE OF MISSING CHILD");


        contentStream.endText();
        contentStream.drawImage(qrImageXObject, qrX, qrY, 200, 200);


        contentStream.close();

        String fileName = "Tag_PlatformTicket_" + TicketID.getText() + ".pdf";

        document.save(fileName);
        document.close();

        // Open the PDF file
        new Thread(() -> {
            try {
                Desktop.getDesktop().open(new File(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

  private void PrintTicket() throws IOException{

            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            doc.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName font_name_3v= org.apache.pdfbox.pdmodel.font.Standard14Fonts.getMappedFontName("COURIER");
             PDFont pdfFont=  new PDType1Font(font_name_3v.COURIER);
             contentStream.setFont(pdfFont,13);
             float fontSize = 13;


            contentStream.setFont(pdfFont, fontSize);

            // Write text fields content to the PDF
            contentStream.beginText();
            contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 50);
            java.time.LocalTime TicketPrintingTime =  java.time.LocalTime.parse(Time.getText());

            String[] lines = {
                "SASA RAILWAYS PLATFORM TICKET",
                "Ticket ID: " + TicketID.getText(),
                "Date: " + Date.getText(),
                "Person(s): " + NoOfPersons.getText(),
                "Platform Number: " + PlatformNumber.getText(),
                "Amount: Rs." + Amount.getText(),
                "Time: " + Time.getText(),
                "Valid for 2 hours only .. till ",
                TicketPrintingTime.plusHours(2).toString()
            };
            

            float maxWidth = 0;
            for (String line : lines) {
                float width = fontSize * pdfFont.getStringWidth(line) / 1000;
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }

            float CENTER = 1.5f * fontSize;
            float startX = 50;
            float startY = page.getMediaBox().getHeight() - 50;

            for (String line : lines) {
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -CENTER);
            }
            contentStream.endText();

            float borderPadding = 10;
            float borderX = startX - borderPadding;
            float borderY = startY + borderPadding;
            float borderWidth = maxWidth + 2 * borderPadding;
            float borderHeight = (lines.length * CENTER) + 2 * borderPadding;

            // Draw the border
            contentStream.setStrokingColor(0,0,0);
            contentStream.setLineWidth(0.5f);
            contentStream.addRect(borderX, borderY - borderHeight, borderWidth, borderHeight);
            contentStream.stroke();

            contentStream.close();

            // Save the PDF
            String fileName = "PlatformTicket_" + Date.getText() + Time.getText()+ java.time.LocalTime.now().getSecond() + ".pdf";
            doc.save(fileName);
            doc.close();

            // Open the saved PDF file
            new Thread(() -> {
                try {
                    Desktop.getDesktop().open(new File(fileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
    }
  

   private void PopulateGuestInfo() throws SQLException{
    
    for ( GuestDetails x: PassengerInformation.getPassengerInformation().values() ){
            Connection conn = null;
            try {
                   
                    // Create a connection to the database
                     conn = DBConn.connect();
        
                    // Prepare the SQL statement
                    String sql = "INSERT INTO guest_info(ticket_id,name,mobile_no) VALUES (?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1,BankPage.TransacID);
                    pstmt.setString(2, x.returnName());
                    pstmt.setString(3, x.returnPhoneNumber());
                    int result = pstmt.executeUpdate();
                    if(result == 0){
                        System.out.println("No rows updated");
                        throw new SQLException("Update into guest table did not update any rows");
                    }
                    // pstmt.setString(2, DateTextField.getText());
                    pstmt.close();
                    conn.close();
    }
    catch(SQLException e ){
           throw e;
    }
    
    }
    }
  void PopulatePlatformInfo()throws SQLException{
        Connection conn = DBConn.connect();

            // Prepare the SQL statement
            String sql = "INSERT INTO platform_ticket_info (ticket_id, numberofpersons, platform_no, amount, time, pnr_no, transaction_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, TicketID.getText());
            pstmt.setInt(2, Integer.parseInt(NoOfPersons.getText()));
            pstmt.setInt(3, Integer.parseInt(PlatformNumber.getText()));
            pstmt.setDouble(4, Double.parseDouble(Amount.getText()));
            pstmt.setString(6, GuestDetails.PNR);
            pstmt.setString(7, BankPage.TransacID);
            Calendar calendar = Calendar.getInstance();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(calendar.getTime().getTime());
            pstmt.setTimestamp(5, timeStamp);

            // Execute the SQL statement
            pstmt.executeUpdate();

            // Close the database connection
            conn.close();
      
  }
}


/**
 * Encapsulates custom configuration used in methods of {@link MatrixToImageWriter}.
 */
 final class MatrixToImageConfig {

  public static final int BLACK = 0xFF000000;
  public static final int WHITE = 0xFFFFFFFF;
  
  private final int onColor;
  private final int offColor;

  /**
   * Creates a default config with on color {@link #BLACK} and off color {@link #WHITE}, generating normal
   * black-on-white barcodes.
   */
  public MatrixToImageConfig() {
    this(BLACK, WHITE);
  }

  /**
   * @param onColor pixel on color, specified as an ARGB value as an int
   * @param offColor pixel off color, specified as an ARGB value as an int
   */
  public MatrixToImageConfig(int onColor, int offColor) {
    this.onColor = onColor;
    this.offColor = offColor;
  }

  public int getPixelOnColor() {
    return onColor;
  }

  public int getPixelOffColor() {
    return offColor;
  }

  int getBufferedImageColorModel() {
    if (onColor == BLACK && offColor == WHITE) {
      // Use faster BINARY if colors match default
      return BufferedImage.TYPE_BYTE_BINARY;
    }
    if (hasTransparency(onColor) || hasTransparency(offColor)) {
      // Use ARGB representation if colors specify non-opaque alpha
      return BufferedImage.TYPE_INT_ARGB;
    }
    // Default otherwise to RGB representation with ignored alpha channel
    return BufferedImage.TYPE_INT_RGB;
  }

  private static boolean hasTransparency(int argb) {
    return (argb & 0xFF000000) != 0xFF000000;
  }

}


 final class MatrixToImageWriter {

    private static final MatrixToImageConfig DEFAULT_CONFIG = new MatrixToImageConfig();
  
    private MatrixToImageWriter() {}
  
    /**
     * Renders a {@link BitMatrix} as an image, where "false" bits are rendered
     * as white, and "true" bits are rendered as black. Uses default configuration.
     *
     * @param matrix {@link BitMatrix} to write
     * @return {@link BufferedImage} representation of the input
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
      return toBufferedImage(matrix, DEFAULT_CONFIG);
    }
  
    /**
     * As {@link #toBufferedImage(BitMatrix)}, but allows customization of the output.
     *
     * @param matrix {@link BitMatrix} to write
     * @param config output configuration
     * @return {@link BufferedImage} representation of the input
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix, MatrixToImageConfig config) {
      int width = matrix.getWidth();
      int height = matrix.getHeight();
      BufferedImage image = new BufferedImage(width, height, config.getBufferedImageColorModel());
      int onColor = config.getPixelOnColor();
      int offColor = config.getPixelOffColor();
      int[] rowPixels = new int[width];
      BitArray row = new BitArray(width);
      for (int y = 0; y < height; y++) {
        row = matrix.getRow(y, row);
        for (int x = 0; x < width; x++) {
          rowPixels[x] = row.get(x) ? onColor : offColor;
        }
        image.setRGB(0, y, width, 1, rowPixels, 0, width);
      }
      return image;
    }
  
    /**
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param file file {@link File} to write image to
     * @throws IOException if writes to the file fail
     * @deprecated use {@link #writeToPath(BitMatrix, String, Path)}
     */
    @Deprecated
    public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
      writeToPath(matrix, format, file.toPath());
    }
  
    /**
     * Writes a {@link BitMatrix} to a file with default configuration.
     *
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param file file {@link Path} to write image to
     * @throws IOException if writes to the stream fail
     * @see #toBufferedImage(BitMatrix)
     */
    public static void writeToPath(BitMatrix matrix, String format, Path file) throws IOException {
      writeToPath(matrix, format, file, DEFAULT_CONFIG);
    }
  
    /**
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param file file {@link File} to write image to
     * @param config output configuration
     * @throws IOException if writes to the file fail
     * @deprecated use {@link #writeToPath(BitMatrix, String, Path, MatrixToImageConfig)}
     */
    @Deprecated
    public static void writeToFile(BitMatrix matrix, String format, File file, MatrixToImageConfig config) 
        throws IOException {
      writeToPath(matrix, format, file.toPath(), config);
    }
  
    /**
     * As {@link #writeToPath(BitMatrix, String, Path)}, but allows customization of the output.
     *
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param file file {@link Path} to write image to
     * @param config output configuration
     * @throws IOException if writes to the file fail
     */
    public static void writeToPath(BitMatrix matrix, String format, Path file, MatrixToImageConfig config)
        throws IOException {
      BufferedImage image = toBufferedImage(matrix, config);
      if (!ImageIO.write(image, format, file.toFile())) {
        throw new IOException("Could not write an image of format " + format + " to " + file);
      }
    }
  
    /**
     * Writes a {@link BitMatrix} to a stream with default configuration.
     *
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param stream {@link OutputStream} to write image to
     * @throws IOException if writes to the stream fail
     * @see #toBufferedImage(BitMatrix)
     */
    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
      writeToStream(matrix, format, stream, DEFAULT_CONFIG);
    }
  
    /**
     * As {@link #writeToStream(BitMatrix, String, OutputStream)}, but allows customization of the output.
     *
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param stream {@link OutputStream} to write image to
     * @param config output configuration
     * @throws IOException if writes to the stream fail
     */
    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream, MatrixToImageConfig config) 
        throws IOException {  
      BufferedImage image = toBufferedImage(matrix, config);
      if (!ImageIO.write(image, format, stream)) {
        throw new IOException("Could not write an image of format " + format);
      }
    }
  
  }
  
package gui;


import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import player.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javax.sound.sampled.AudioSystem.getAudioFileTypes;


public class FXMLDocumentController implements Initializable { // наследуем от некого интерфейса Initializable
    // и переопределить одну функцию, которую используется как своего конструктор формы.


    @FXML
    private Slider Slider0, Slider1, Slider2, Slider3, Slider4, Slider5, Slider6, Slider7, Slider8, Slider9,
            soundSlider;
    @FXML
    private LineChart chart1;
    @FXML
    private LineChart chart2;
    @FXML
    private NumberAxis xAxis1, yAxis1, xAxis2, yAxis2;
    private AudioPlayer audioPlayer;
    private Thread playThread, plotThread;
    //Поток в Java представлен в виде экземпляра класса java.lang.Thread.
    // Стоит сразу понимать, что экземпляры класса Thread в Java сами по себе не являются потоками.
    // Это лишь своего рода API для низкоуровневых потоков, которыми управляет JVM и операционная система
    @FXML
    CheckBox EchoBox, EnvelopeBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) { // вот функция инициализации формы, в нее все писать будем

        this.listenSliders();
        this.initialGraph();
        this.checkBoxInnitial();
        this.volumeFromSlider();
    }

    @FXML
    private void open() throws FileNotFoundException, IOException, LineUnavailableException, UnsupportedAudioFileException, InterruptedException {
        //Выбор файлов формата wav
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
              new ExtensionFilter("Audio Files", "*.wav"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if(selectedFile == null) return;

        this.audioPlayer = new AudioPlayer(selectedFile);
        playThread = new Thread(()->{
            this.audioPlayer.play();
        });
        playThread.start();

        System.out.println("PLAY");
    }

    @FXML
    private void play() {
        if (this.audioPlayer != null)
            this.audioPlayer.setPause(false);
    }

    @FXML
    private void pause() {
        if (this.audioPlayer != null)
            this.audioPlayer.setPause(true); //установка паузы
    }

    @FXML
    private void stop() {
        if (this.audioPlayer == null) return;
        Slider0.setValue(1);
        Slider1.setValue(1);
        Slider2.setValue(1);
        Slider3.setValue(1);
        Slider4.setValue(1);
        Slider5.setValue(1);
        Slider6.setValue(1);
        Slider7.setValue(1);
        Slider8.setValue(1);
        Slider9.setValue(1);

        soundSlider.setValue(0.25);
    }

    @FXML
    private void EchoBox() throws IOException, InterruptedException{
        System.out.println("Echo");
        if(!this.audioPlayer.EchoIsActive())
            this.audioPlayer.setEcho(true);
        else this.audioPlayer.setEcho(false);
    }

    @FXML
    private void EnvelopeBox(){
        System.out.println("Envelope");
        if(!this.audioPlayer.EnvelopeIsActive())
            this.audioPlayer.setEnvelope(true);
        else this.audioPlayer.setEnvelope(false);
    }

    @FXML
    private void clickClose() {
        if(this.audioPlayer != null) {
            if(this.playThread != null)
                this.playThread.interrupt();
            this.audioPlayer.getEqualizer().close();
            this.audioPlayer.close();
        }

        System.exit(0);
    }

    //Метод, осуществляющий прослушку слайдеров и изменяющий КУ полос эквалайзера
    private void listenSliders(){

        Slider0.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer().getFilter((short)0).setGain((float)newValue.doubleValue());
        });

        Slider1.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer().getFilter((short)1).setGain((float)newValue.doubleValue());
        });

        Slider2.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer().getFilter((short)2).setGain((float)newValue.doubleValue());
        });

        Slider3.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer().getFilter((short)3).setGain((float)newValue.doubleValue());
        });

        Slider4.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer().getFilter((short)4).setGain((float)newValue.doubleValue());
        });

        Slider5.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer().getFilter((short)5).setGain((float)newValue.doubleValue());
        });

        Slider6.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer().getFilter((short)6).setGain((float)newValue.doubleValue());
        });

        Slider7.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer().getFilter((short)7).setGain((float)newValue.doubleValue());
        });

    }


    private XYChart.Data[] oldData; //это класс для задания данных графику
    private XYChart.Data[] newData;
    //Метод, осуществляющий инициализацию графиков
    private void initialGraph(){
        XYChart.Series oldGraph = new XYChart.Series<>();
        XYChart.Series newGraph = new XYChart.Series<>();
        int size = 512;
        oldData = new XYChart.Data[size];
        newData = new XYChart.Data[size];
        for (int i = 0; i < oldData.length; i++){
            oldData[i] = new XYChart.Data<>(i, 0);
            oldGraph.getData().add(oldData[i]);

            newData[i] = new XYChart.Data<>(i, 0);
            newGraph.getData().add(newData[i]);
        }

        chart1.getData().add(oldGraph);
        chart2.getData().add(newGraph);
        chart1.setCreateSymbols(false);
        chart2.setCreateSymbols(false);
        chart1.setAnimated(false);
        chart2.setAnimated(false);

        this.chart1.getYAxis();
        this.yAxis1.setLowerBound(-0.2);
        this.yAxis1.setUpperBound(0.3);
        this.yAxis1.setAnimated(false);

        this.chart2.getYAxis();
        this.yAxis2.setLowerBound(-0.2);
        this.yAxis2.setUpperBound(0.3);
        this.yAxis2.setAnimated(false);
    }

    private void checkBoxInnitial() {
        this.EchoBox = new CheckBox();
        this.EchoBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        });

        this.EnvelopeBox = new CheckBox();
        this.EnvelopeBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        });
    }

    private void volumeFromSlider() {
        soundSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            audioPlayer.setVolume(newValue.doubleValue());
        });
    }

    private boolean graphFlag = false;
    @FXML
    private void clickPlot(){
        if (this.graphFlag == false){
            this.graphFlag = true;
        }
        else this.graphFlag = false;
        this.plotThread = new Thread(()->{
            while(this.graphFlag){
                if(this.graphFlag == false)
                    for(;;){
                        try{
                            if(this.graphFlag == true) break;
                            this.plotThread.sleep(50);
                        }
                        catch(Exception e){
                        }
                    }

                if(audioPlayer.isCalculated){
                    for(int j = 0; j < this.audioPlayer.getFourierInput().length; j++){
                        this.oldData[j].setYValue(Math.log10(this.audioPlayer.getFourierInput()[j] * 0.1) / 10);
                        this.newData[j].setYValue(Math.log10(this.audioPlayer.getFourierOutput()[j] * 0.1) / 10);
                    }
                }
                try {
                    this.plotThread.sleep(60);
                }
                catch (Exception e) {
                }
            }
        });
        plotThread.start();
    }
}

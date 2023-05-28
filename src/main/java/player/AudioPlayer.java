package player;

import effects.Echo;
import effects.Envelope;
import equalizer.Equalizer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class AudioPlayer implements LineListener{
    private double volume;
    private final SourceDataLine sourceDataLine;
    private final AudioInputStream ais;
    private final byte[] buff;

    public boolean isCalculated = false;
    private final int BUFF_SIZE = 16384;
    //private final int BUFF_SIZE = 131072;
    //private final int BUFF_SIZE = 65536;
    //private final int BUFF_SIZE = 8096;

    private short[] sampleBuff;

    private final FFT fourierInput;
    public FFT fourierOutput;

    private final Echo Echo;
    private boolean isEcho;

    private final Envelope Envelope;
    private double EnvelopeCoef;
    private boolean isEnvelope;

    private final Equalizer equalizer;
    private boolean pause;
    private final AudioFormat format;

    private int counter=0;

    public AudioPlayer(File musicFile) throws UnsupportedAudioFileException,
            IOException, InterruptedException, LineUnavailableException {
        ReadMusicFile readFile = new ReadMusicFile(musicFile);
        this.sourceDataLine =  readFile.getSourceDataLine();
        this.ais = readFile.getAudioInputStream();
        this.buff = new byte[this.BUFF_SIZE];
        this.sampleBuff = new short[BUFF_SIZE / 2];
        this.Echo = new Echo();
        this.Envelope = new Envelope();
        this.isEcho = false;
        this.isEnvelope = false;
        this.EnvelopeCoef = 1.0;
        this.equalizer = new Equalizer(BUFF_SIZE / 2);
        AudioFileFormat aff = new AudioFileFormat();
        format = new AudioFormat((float)aff.getSampleRate(),
                aff.getBits(), aff.getChannels(),
                aff.isSigned(), aff.isBigEndian());
        this.volume = 0.25;
        this.fourierInput = new FFT();
        this.fourierOutput = new FFT();
    }



    public void play() {
        try{
            this.sourceDataLine.open(this.format);
            this.sourceDataLine.start();
            this.pause = false;
            while ((this.ais.read(this.buff, 0, this.BUFF_SIZE)) != -1) {
                this.ByteArrayToSamplesArray();
                this.isCalculated = false;
                if(this.pause) {this.stop();}

                if(this.isEcho)
                    this.Echo(this.sampleBuff);

                if(this.isEnvelope) {
                    this.Envelope(sampleBuff);
                }
                this.fourierInput.FFTAnalysis(this.sampleBuff, 512);
                equalizer.setInputSignal(this.sampleBuff);
                this.equalizer.equalization();
                this.sampleBuff = equalizer.getOutputSignal();
                this.fourierOutput.FFTAnalysis(this.sampleBuff, 512);
                this.isCalculated = true;
                this.SampleArrayByteArray();
                sourceDataLine.write(this.buff, 0, this.buff.length - 0 );
            }
            System.out.println("END");
            this.isCalculated = false;
            this.sourceDataLine.drain();
            this.sourceDataLine.close();
        }catch (LineUnavailableException | IOException | InterruptedException | ExecutionException e) {
        }
    }


    private void Echo(short[] inputSamples) {
        this.Echo.setInputSampleStream(inputSamples);
        this.Echo.createEffect();
    }

    public boolean EchoIsActive() {
        return this.isEcho;
    }

    public void setEcho(boolean b) {
        this.isEcho = b;
    }

    private void Envelope(short[] inputSamples) {
        this.Envelope.setInputSampleStream(inputSamples);
        this.Envelope.createEffect();
    }

    public boolean EnvelopeIsActive() {
        return this.isEnvelope;
    }

    public void setEnvelope(boolean b) {
        this.isEnvelope = b;
    }


    private void stop() {
        if(pause) {
            for(;;) {
                try {
                    if(!pause) break;
                    this.isCalculated = false;
                    Thread.sleep(50);
                }
                catch (InterruptedException e) {
                }
            }
        }
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean getPause() {
        return this.pause;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getVolume() {
        return this.volume;
    }

    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
    }

    public short[] getSamples() {
        return this.sampleBuff;
    }

    private void ByteArrayToSamplesArray() {
        for(int i = 0, j = 0; i < this.buff.length; i += 2 , j++) {
            this.sampleBuff[j] = (short) (0.5 *  (ByteBuffer.wrap(this.buff, i, 2).order(
                    java.nio.ByteOrder.LITTLE_ENDIAN).getShort()) * this.getVolume());
        }
    }

    private void SampleArrayByteArray() {
        for(int i = 0, j = 0; i < this.sampleBuff.length && j < (this.buff.length); i++, j += 2 ) {
            this.buff[j] = (byte)(this.sampleBuff[i]);
            this.buff[j + 1] = (byte)(this.sampleBuff[i] >>> 8);
//
        }
    }

    public Equalizer getEqualizer() {
        return this.equalizer;
    }

    public void close() {
        if(this.ais != null)
            try {
                this.ais.close();
            }
            catch (IOException e) {
            }
        if(this.sourceDataLine != null)
            this.sourceDataLine.close();
    }

    public double[] getFourierInput(){
        return this.fourierInput.getFFTData();
    }
    public double[] getFourierOutput(){
        return this.fourierOutput.getFFTData();
    }
}


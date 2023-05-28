package effects;

public class Echo extends Effect{

    public Echo() {
        super();
    }

    public void setInputSampleStream(short[] inputAudioStream) {
        this.inputAudioStream = inputAudioStream;
    }

    @Override
    public synchronized short[] createEffect() {
/*
        short amplitude;
        short amplitudex;
        short EchoStart;
        int checkFlag;
        int Chor = 3000;
        int position = 0;

        for(int i = Chor ; i < this.inputAudioStream.length-200; i ++) {
            amplitude = this.inputAudioStream[i];
            amplitudex = this.inputAudioStream[i+200];
            ChorAmplitude = this.inputAudioStream[position];
            checkFlag = ( (( ChorAmplitude) + (int)(0.7 * amplitude)+(int)(0.6 * amplitudex)));
            if(checkFlag < Short.MAX_VALUE && checkFlag > Short.MIN_VALUE) {
                ChorAmplitude = (short)checkFlag;
                this.inputAudioStream[position] =  ChorAmplitude;
                position += 1;
            }
        }
         */
        short amplitude;
        short EchoStart;
        int checkFlag;
        int range = 4000;
        int position = 0;
        for(int i = position+range ; i < this.inputAudioStream.length; i ++) {
            amplitude = this.inputAudioStream[i];
            EchoStart = this.inputAudioStream[position];
            checkFlag = ( ((amplitude) + (int)(0.7 * EchoStart)));
            if(checkFlag < Short.MAX_VALUE && checkFlag > Short.MIN_VALUE) {
                amplitude = (short)checkFlag;
                this.inputAudioStream[i] =  amplitude;
                position += 1;
            }
        }
        return this.inputAudioStream;
    }

    @Override
    public synchronized short[] getOutputAudioStream() {
        return this.inputAudioStream;
    }
}


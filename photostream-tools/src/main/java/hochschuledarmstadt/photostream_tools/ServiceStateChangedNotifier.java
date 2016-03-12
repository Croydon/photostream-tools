package hochschuledarmstadt.photostream_tools;

/**
 * Created by Andreas Schattney on 11.03.2016.
 */
public interface ServiceStateChangedNotifier {
    void addOnServiceStateChangedListener(OnServiceStateChangedListener onServiceStateChangedListener);
    void removeOnServiceStateChangedListener(OnServiceStateChangedListener onServiceStateChangedListener);
}

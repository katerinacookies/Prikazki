public class Tale {
    String name;
    int id;
    int group;
    String soundsPath;
    String picsPath;
    String[] animationsPaths;

    public Tale(String name, int id, int group, String soundsPath, String picsPath, String[] animationsPaths) {
        this.name = name;
        this.id = id;
        this.group = group;
        this.soundsPath = soundsPath;
        this.picsPath = picsPath;

        for (int i = 0; i < animationsPaths.length; ++i) {
            this.animationsPaths[i] = animationsPaths[i];
        }
    }

    public static Tale GetTaleFromJSON(String JSON) {

    }
}
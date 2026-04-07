public class JSONReader {
    public String getTaleJSONData(Context context, int targTaleId) {
        try {
            String jsonString = loadJSONFromAsset(context, "example.json");

            JSONArray a = new JSONArray(jsonString);

            for (int i = 0; i < a.length(); i++) {
                JSONObject tale = a.getJSONObject(i);

                int currTaleId = tale.getInt("id");

                if (currTaleId == targTaleId) {
                    return tale.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private String loadJSONFromAsset(Context context, String fileName) throws Exception {
        java.io.InputStream is = context.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, "UTF-8");
    }
}
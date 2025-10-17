package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        DogApiBreedFetcher breedFetcher = new DogApiBreedFetcher();
        String url = "https://dog.ceo/api/breed/" + breed + "/list";
        try {
            String response = breedFetcher.run(url);
            JSONObject obj = new JSONObject(response);
            ArrayList<String> subbreeds = new ArrayList<>();
            try{
                JSONArray obj_array = obj.getJSONArray("message");
                for (int i = 0; i < obj_array.length(); i++) {
                    subbreeds.add(obj_array.getString(i));
                }
                return subbreeds;
            }
            catch (JSONException e) {
                throw new BreedNotFoundException(breed);
            }

        } catch (IOException e) {
            throw new BreedNotFoundException(breed);
        }
    }
}
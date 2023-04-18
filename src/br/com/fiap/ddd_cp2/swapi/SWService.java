package br.com.fiap.ddd_cp2.swapi;

import br.com.fiap.ddd_cp2.swapi.beans.People;
import br.com.fiap.ddd_cp2.swapi.beans.PeoplePage;
import br.com.fiap.ddd_cp2.swapi.exceptions.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SWService {
    private static SWService instance;
    private final List<People> peopleList;

    private SWService() throws PeoplePageError {
        peopleList = getAllPersons();
    }

    public static SWService getInstance() throws SWServiceError {
        try {
            if (instance == null) {
                instance = new SWService();
            }

            return instance;
        } catch (PeoplePageError e) {
            throw new SWServiceError(e.getMessage());
        }
    }

    private List<People> getPersonsFromPage(int page) throws PeoplePageNotFound, PeoplePageError {
        String url = "https://swapi.dev/api/people?format=json&page=" + page;
        HttpGet request = new HttpGet(url);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                if (entity != null) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Integer.class, new People.CustomNumberDeserializer()).create();
                    String jsonResponse = EntityUtils.toString(entity);
                    PeoplePage peoplePage = gson.fromJson(jsonResponse, PeoplePage.class);
                    return peoplePage.getResults();
                }
            } else if (statusLine.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new PeoplePageNotFound(url);
            } else {
                throw new PeoplePageError("Unknown error when fetching API");
            }
        } catch (IOException | PeoplePageError e) {
            throw new PeoplePageError(e.getMessage());
        }

        return Collections.emptyList();
    }

    private List<People> getAllPersons() throws PeoplePageError {
        List<People> peopleList = new ArrayList<People>();

        for (int page = 1; ; page++) {
            try {
                peopleList.addAll(getPersonsFromPage(page));
            } catch (PeoplePageNotFound e) {
                break;
            }
        }

        return peopleList;
    }

    public List<People> getPeopleList() {
        return peopleList;
    }

    public List<People> searchForName(String name) throws PeopleNotFound, InvalidSearchString {
        if (!name.matches("[a-zA-Z0-9\\s-]+")) {
            throw new InvalidSearchString(name);
        }

        List<People> peopleList1 = new ArrayList<People>();

        for (People people : peopleList) {
            if (people.getName().toLowerCase().contains(name.toLowerCase())) {
                peopleList1.add(people);
            }
        }

        if (peopleList1.size() == 0) {
            throw new PeopleNotFound(String.format("Not found people with name that's contains [%s]", name));
        }

        return peopleList1;
    }

    public People getPeopleByIndex(Integer index) throws PeopleNotFound {
        if (index >= peopleList.size()) {
            throw new PeopleNotFound(String.format("Not found people with index %d", index));
        }

        return peopleList.get(index);
    }
}

package br.com.fiap.ddd_cp2.main;

import br.com.fiap.ddd_cp2.swapi.SWService;
import br.com.fiap.ddd_cp2.swapi.beans.People;
import br.com.fiap.ddd_cp2.swapi.exceptions.InvalidSearchString;
import br.com.fiap.ddd_cp2.swapi.exceptions.PeopleNotFound;
import br.com.fiap.ddd_cp2.swapi.exceptions.SWServiceError;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

enum MainMenuOption {
    INVALID_OPTION(null),
    LIST_ALL_PEOPLES(1),
    SEARCH_BY_NAME(2),
    GET_PEOPLE_BY_INDEX(3);

    private final Integer option;

    public Integer getInteger() {
        return option;
    }

    MainMenuOption(Integer option) {
        this.option = option;
    }

    public static MainMenuOption fromInteger(Integer optionSelected) {
        for (MainMenuOption option : MainMenuOption.values()) {
            if (Objects.equals(option.getInteger(), optionSelected)) {
                return option;
            }
        }

        return INVALID_OPTION;
    }
}

public class Main {
    private static SWService swService;

    static {
        try {
            swService = SWService.getInstance();
        } catch (SWServiceError e) {
            showErrorMessage("Não foi possível iniciar o serviço da API StarWars.");
            System.exit(-1);
        }
    }

    private static void displayMessage(String msg) {
        System.out.println(msg);
    }

    private static void displayMessage(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static void displayPeople(People people) {
        displayMessage("Nome: %s", people.getName());
        displayMessage("Gênero: %s", people.getGender());
        displayMessage("Cor do cabelo: %s", people.getHairColor());
        displayMessage("Cor dos olhos: %s", people.getEyeColor());
        displayMessage("Ano de nascimento: %s", people.getBirthYear());

        Integer height = people.getHeight();
        if (height != null) {
            displayMessage("Altura : %d cm", height);
        } else {
            displayMessage("Altura: unknown");
        }

        Integer weight = people.getMass();
        if (weight != null) {
            displayMessage("Peso: %d kg", weight);
        } else {
            displayMessage("Peso: unknown");
        }
        displayMessage("");
    }

    private static void displayListOfPeoples(List<People> peopleList) {
        displayMessage("Exibindo %d pessoas:", peopleList.size());
        for (People people : peopleList) {
            displayPeople(people);
        }
    }

    private static void showErrorMessage(String msg) {
        JOptionPane.showConfirmDialog(null, msg, "Erro", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
    }

    private static Integer getInteger(String msg) {
        try {
            return Integer.parseInt(JOptionPane.showInputDialog(msg));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static MainMenuOption getMainMenuOption() {
        String msg = """
            Digite uma opção (digite qualquer outra para sair):
            1 - Listar todas as pessoas
            2 - Procurar por nome
            3 - Buscar por índice
            """;
        return MainMenuOption.fromInteger(getInteger(msg));
    }

    private static void listAllPeoples() {
        displayListOfPeoples(swService.getPeopleList());
    }

    private static void searchByName() {
        String searchString = JOptionPane.showInputDialog("Pesquisar por:");

        try {
            displayListOfPeoples(swService.searchForName(searchString));
        } catch (InvalidSearchString e) {
            showErrorMessage("Termo de pesquisa inválido, apenas letras, número, hifens e espaços são permitidos.");
        } catch (PeopleNotFound e) {
            showErrorMessage("Nenhuma pessoa encontrada com o termo pesquisado.");
        }
    }

    private static void searchByIndex() {
        try {
            int searchIndex = Integer.parseInt(JOptionPane.showInputDialog("Buscar índice:"));
            displayPeople(swService.getPeopleByIndex(searchIndex));
        } catch (NumberFormatException e) {
            showErrorMessage("Índice inserido inválido, por favor verifique a entrada.");
        } catch (PeopleNotFound e) {
            showErrorMessage("Nenhuma pessoa encontrada com o índice informado.");
        }
    }

    private static void mainLoop() {
        while (true) {
            MainMenuOption option = getMainMenuOption();

            if (option == MainMenuOption.INVALID_OPTION) {
                break;
            }

            switch (option) {
                case LIST_ALL_PEOPLES -> listAllPeoples();
                case SEARCH_BY_NAME -> searchByName();
                case GET_PEOPLE_BY_INDEX -> searchByIndex();
            }
        }
    }

    public static void main(String[] args) {
        mainLoop();
    }
}

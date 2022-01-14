package br.usp.each.typerace.server;

import java.util.ArrayList;

public class Player {
    private String name;
    private int corrects, incorrects;
    private ArrayList<String> list;

    Player(String name) {
        this.name = name;
        this.corrects = 0;
        this.incorrects = 0;
        this.list = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public int getCorrects() {
        return corrects;
    }

    public int getIncorrects() {
        return incorrects;
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> words) {
        list.clear();       
        list.addAll(words);
    }

    public void removeWord(String word) {
        list.remove(word);
    }

    public void addCorrect() {
        corrects++;
    }

    public void addIncorrect() {
        incorrects++;
    }

    public void resetScore() {
        corrects = 0;
        incorrects = 0;
    }
}
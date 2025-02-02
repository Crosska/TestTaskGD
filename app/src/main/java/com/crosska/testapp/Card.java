package com.crosska.testapp;

import java.util.List;

public class Card {

    private int id;
    private int inner_id;
    private int attack;
    private int hp;
    private List<String> skillsNames;
    private List<String> skillsValues;
    private String image;
    private String rarity;
    private String gender;
    private String race;
    private String class_;
    private List<String> promoteTypes;
    private List<String> promoteValues;
    private boolean isEvent;
    private int power;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInner_id() {
        return inner_id;
    }

    public void setInner_id(int inner_id) {
        this.inner_id = inner_id;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public List<String> getSkillsNames() {
        return skillsNames;
    }

    public void setSkillsNames(List<String> skillsNames) {
        this.skillsNames = skillsNames;
    }

    public List<String> getSkillsValues() {
        return skillsValues;
    }

    public void setSkillsValues(List<String> skillsValues) {
        this.skillsValues = skillsValues;
    }

    public void setSkillsValue(int i, String val) {
        this.skillsValues.set(i, val);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }

    public List<String> getPromoteTypes() {
        return promoteTypes;
    }

    public void setPromoteTypes(List<String> promoteTypes) {
        this.promoteTypes = promoteTypes;
    }

    public List<String> getPromoteValues() {
        return promoteValues;
    }

    public void setPromoteValues(List<String> promoteValues) {
        this.promoteValues = promoteValues;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean event) {
        isEvent = event;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}

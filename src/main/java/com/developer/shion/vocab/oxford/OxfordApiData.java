package com.developer.shion.vocab.oxford;

public class OxfordApiData {
    private String keyword;
    private String response;

    public OxfordApiData(String keyword, String response){
        this.keyword = keyword;
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

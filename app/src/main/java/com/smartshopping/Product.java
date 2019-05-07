package com.smartshopping;

public class Product {

    public int id = 0;
    public String name = "";
    public int price = 0;
    public int count = 1;
    public int img = 0;
    public String image;

    public Product() {}

    public Product(String name, int price, int img) {
        this.name = name;
        this.price = price;
        this.img = img;
    }
}

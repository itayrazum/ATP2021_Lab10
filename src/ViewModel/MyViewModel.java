package ViewModel;

import Model.IModel;


public class MyViewModel {
    private IModel model;



    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this); //Observe the Model for it's changes
    }

}

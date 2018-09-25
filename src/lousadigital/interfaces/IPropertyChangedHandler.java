/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.interfaces;

/**
 *
 * @author gabri
 */
@FunctionalInterface
public interface IPropertyChangedHandler
{
    public void onPropertyChanged(String propertyName, Object newValue);
}

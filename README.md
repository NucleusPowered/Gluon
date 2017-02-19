# Gluon
A bridge that links the Nucleus and PlaceholderAPI systems together

## How to install

First, ensure you have the following plugins available:

* Nucleus, at least version 0.24.0
* PlaceholderAPI, at least version 3.5

Drop it into your mods directory and start your server!

## Using PlaceholderAPI tokens in Nucleus

Anywhere that tokens can be used in Nucleus, PlaceholderAPI tokens can be used! However, there is one major difference, whilst 
PlaceholderAPI normally uses the `% %` token format, Nucleus instead requires PlaceholderAPI tokens to be of the form `{% %}` for
compatibility reasons.

So, to use the `%PLAYER%` token in Nucleus, instead use `{%PLAYER%}`. You can also use `{{pl:nucleus-gluon:player}}` if you prefer.

## Using Nucleus tokens with plugins that use PlaceholderAPI

The Nucleus token is `nucleus_[token]` where `[token]` is the normal Nucleus token without `{{ }}` characters.
 
Examples are: 

* `{{displayname}}` would be `%nucleus_displayname%` 
* `{{pl:plugin:id}}` would be `%nucleus_pl:plugin:id%`
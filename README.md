# A MVVM sample architecture
<p align="center">
<img src="https://i.imgur.com/K0Xm7QU.png"/>
</p>

A MVVM sample architecture using a clean code structure. 
Please consider the project is the result of specific exercise requirements, some choices can sound strange, for example a specific requirement was to fetch and save dollar only rate, and execute the change rate using an algorithm
The project use openexchangerate api to fetch currencies rate, if you want to use a different key (or the one inside the project is expired), take a look at app/build.gradle, you can change the value there.

In this project you'll find :

* An hybrid MVVM/clean code architectural pattern, using views-> viewmodels -> usecases -> repositories -> models
* Material 3
* Dependency injection using **[Hilt](https://developer.android.com/training/dependency-injection/hilt-android)**
* **[Room](https://developer.android.com/training/data-storage/room)** for persistence
* **[Retrofit](https://square.github.io/retrofit/)** for networking
* **[Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** for asynchronous operations
* **[LiveData](https://developer.android.com/topic/libraries/architecture/livedata)** and ui states handling for loading/success/error
* Unit testing

## Why a currency converter app?

The app is the result of an interview exercise (done on codility.com), by the way I think the project is simple enough to quickly understand the advantages of a MVVM and Clean Code architectural pattern, but complex enough to showcase different scenarios and decisions.

## What is it not?

*   A UI/Material Design sample. The interface of the app is deliberately kept simple to focus on architecture.
*   A Jetpack Compose sample. I feel more confident using XML layouts and I believe Compose is not mature enough to replace them.
*   A real production app, consider it just a showcase of an updated mvvm sample using clean code structure and some great (jetpack) libraries.

## Who is it for?

*   Anyone looking for a way to structure a robust android application that is easy to evolve and maintain
*   A quick reference for a mvvm clean architecture

## The project

Clone the repository:

```
git clone https://github.com/nicolacaferra/architecture_sample_currency_converter.git
```

## The app 
![App sample](https://nicolacaferra.dev/assets/images/2022/app_in_action.gif)
from django.urls import path

from . import views

urlpatterns = [
    path('s/<slug:access_key>/', views.lastUpdated, name='lastUpdated'),
    path('s/<slug:access_key>/new/<int:entry_num>/', views.newEntries, name='newEntries'),
    path('in/form/',views.submitEntry,name='submitEntry'),
]

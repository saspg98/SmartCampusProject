from django.contrib import admin

# Register your models here.
from .models import Feedback,FUpdate

admin.site.register(Feedback)
admin.site.register(FUpdate)

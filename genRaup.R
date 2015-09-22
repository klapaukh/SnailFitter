#! /usr/bin/env Rscript

library(dplyr)

w  = 10
t  = 3
d  = 0
r0 = 1
y0 = 1
rc = 1

r <- function(theta, r0, w){
  r0*w^(theta/(2*pi))
}

y <- function(theta, y0, rc, w, t){
  y0*w^(theta/(2*pi)) + rc*t*(w^(theta/(2*pi)) - 1)  
}

points = data.frame(theta = seq(0,4*pi,0.01)) %>%
  mutate(r = r(theta,r0,w),
         y = y(theta,y0,rc,w,t),
         a = r*sin(theta),
         b = r*cos(theta),
         c = y)

points %>%
  select(a,b,c) %>%
  write.table(file="raupCart.csv",row.names = F, col.names = F, sep=" ")

points %>%
  select(theta,r,y) %>%
  write.table(file="raupCyl.csv",row.names = F, col.names = F, sep=" ")


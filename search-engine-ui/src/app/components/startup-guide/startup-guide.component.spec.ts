import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StartupGuideComponent } from './startup-guide.component';

describe('StartupGuideComponent', () => {
  let component: StartupGuideComponent;
  let fixture: ComponentFixture<StartupGuideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StartupGuideComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StartupGuideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
